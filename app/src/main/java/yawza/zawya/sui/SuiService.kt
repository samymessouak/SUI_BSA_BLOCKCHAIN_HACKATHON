package yawza.zawya.sui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import xyz.mcxross.ksui.Sui
import xyz.mcxross.ksui.account.Account
import xyz.mcxross.ksui.model.Network
import xyz.mcxross.ksui.model.SuiConfig
import xyz.mcxross.ksui.model.SuiSettings
import xyz.mcxross.ksui.ptb.programmableTx
import xyz.mcxross.ksui.ptb.Argument

class SuiService(
    private val packageId: String,
    private val registryId: String,
    private val bech32PrivKey: String,
) {
    private val sui = Sui(SuiConfig(settings = SuiSettings(network = Network.TESTNET)))
    private val signer = Account.import(bech32PrivKey.trim())
    private val client = OkHttpClient()
    private val JSON = "application/json; charset=utf-8".toMediaType()

    // Keep the last created inventoryId in memory so UI can reuse it for minting.
    var inventoryId: String? = null
        private set

    // -----------------------------
    // Inventory
    // -----------------------------

    suspend fun createInventory(): String = withContext(Dispatchers.IO) {
        val txb = programmableTx {
            command {
                moveCall {
                    target = "$packageId::stickers::new_inventory"
                }
            }
        }

        val gasBudget = 100_000_000uL
        val exec = sui.signAndExecuteTransactionBlock(signer, txb, gasBudget)
            ?: error("Sui execution returned null")

        val raw = exec.toString()
        val digest = extractDigest(raw)
            ?: error("Digest not found in result: $raw")

        val objId = getCreatedInventoryId(digest)
            ?: error("No Inventory objectId found for tx $digest")

        inventoryId = objId
        objId
    }

    // -----------------------------
    // Mint (fixed test: Mcdo, 1, 1)
    // -----------------------------

    suspend fun mintTestSticker(inventoryId: String): String = withContext(Dispatchers.IO) {
        val sponsor = "Mcdo"
        val collectionId = 1uL
        val stickerId = 1uL

        val txb = programmableTx {
            command {
                moveCall {
                    target = "$packageId::stickers::mint_sticker"
                    // In ksui 2.0.0 we reference inputs by index.
                    arguments = listOf(
                        Argument.Input(0u), // registry
                        Argument.Input(1u), // inventory
                        Argument.Input(2u), // sponsor
                        Argument.Input(3u), // collectionId (u64)
                        Argument.Input(4u)  // stickerId   (u64)
                    )
                }
            }
        }

        val gasBudget = 100_000_000uL

        // NOTE: ksui:2.0.0 does NOT expose a 4-arg sign method to bind values directly.
        // However, its toString() still carries a digest for the executed block, which we can use
        // to query RPC for created objects. That’s what we do next.
        val exec = sui.signAndExecuteTransactionBlock(signer, txb, gasBudget)
            ?: error("Mint execution returned null")

        val raw = exec.toString()
        val digest = extractDigest(raw)
            ?: error("No digest found for mint result: $raw")

        // Ask the fullnode what objects were created in this tx.
        getCreatedStickerId(digest) ?: "Minted (digest=$digest)"
    }

    // -----------------------------
    // Helpers
    // -----------------------------

    /**
     * Extremely tolerant digest extractor for various toString() shapes.
     * Examples it matches:
     *  - "..., transactionBlock=TransactionBlock(digest=ABC123, ...), ..."
     *  - "... digest=ABC123 ...", possibly with newlines.
     */
    private fun extractDigest(raw: String): String? {
        val cleaned = raw.replace("\n", " ")
        val patterns = listOf(
            Regex("""digest=([^,\s\)]+)"""),                            // digest=XYZ,
            Regex("""TransactionBlock\(digest=([^,\s\)]+)"""),          // TransactionBlock(digest=XYZ
            Regex("""transactionBlock=.*?digest=([^,\s\)]+)""", RegexOption.IGNORE_CASE)
        )
        for (p in patterns) {
            p.find(cleaned)?.groupValues?.getOrNull(1)?.let { return it }
        }
        return null
    }

    /**
     * RPC: given a tx digest, return the created Inventory objectId (if any).
     */
    private fun getCreatedInventoryId(digest: String): String? {
        val payload = """
            {"jsonrpc":"2.0","id":1,"method":"sui_getTransactionBlock",
             "params":["$digest",{"showEffects":true,"showObjectChanges":true}]}
        """.trimIndent()

        val req = Request.Builder()
            .url("https://fullnode.testnet.sui.io:443")
            .post(payload.toRequestBody(JSON))
            .build()

        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) error("RPC failed: ${resp.code}")
            val body = resp.body?.string() ?: return null
            val json = JSONObject(body)
            val objChanges = json.optJSONObject("result")?.optJSONArray("objectChanges") ?: return null
            for (i in 0 until objChanges.length()) {
                val ch = objChanges.getJSONObject(i)
                if (ch.optString("type") == "created" &&
                    ch.optString("objectType").endsWith("::stickers::Inventory")
                ) return ch.optString("objectId")
            }
            return null
        }
    }

    /**
     * RPC: given a tx digest, return the created Sticker objectId (if any).
     */
    private fun getCreatedStickerId(digest: String): String? {
        val payload = """
            {"jsonrpc":"2.0","id":1,"method":"sui_getTransactionBlock",
             "params":["$digest",{"showEffects":true,"showObjectChanges":true}]}
        """.trimIndent()

        val req = Request.Builder()
            .url("https://fullnode.testnet.sui.io:443")
            .post(payload.toRequestBody(JSON))
            .build()

        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) error("RPC failed: ${resp.code}")
            val body = resp.body?.string() ?: return null
            val json = JSONObject(body)
            val objChanges = json.optJSONObject("result")?.optJSONArray("objectChanges") ?: return null
            for (i in 0 until objChanges.length()) {
                val ch = objChanges.getJSONObject(i)
                if (ch.optString("type") == "created" &&
                    ch.optString("objectType").endsWith("::stickers::Sticker")
                ) return ch.optString("objectId")
            }
            return null
        }
    }
}
module stickers::stickers {
    use std::string::{Self as string, String};
    use std::vector;
    use sui::object;
    use sui::object::{UID, ID};
    use sui::transfer;
    use sui::tx_context::{Self as txc, TxContext};

    /// =========================
    ///      DATA STRUCTURES
    /// =========================

    public struct Sticker has key, store {
        id: UID,
        sponsor: String,
        collection_id: u64,
        sticker_id: u64,
    }

    public struct Reward has key {
        id: UID,
        sponsor: String,
        collection_id: u64,
        description: String,
    }

    public struct Registry has key {
        id: UID,
        sponsors: vector<String>,
        max_stickers: vector<u64>,
    }

    public struct Inventory has key {
        id: UID,
        owner: address,
        items: vector<Sticker>,
    }

    /// =========================
    ///     REGISTRY LIFECYCLE
    /// =========================

    entry fun init_registry(ctx: &mut TxContext) {
        let reg = Registry {
            id: object::new(ctx),
            sponsors: vector::empty<String>(),
            max_stickers: vector::empty<u64>(),
        };
        transfer::share_object(reg)
    }

    public entry fun append_sponsor(reg: &mut Registry, name: String) {
        vector::push_back(&mut reg.sponsors, name);
    }

    public entry fun remove_sponsor(reg: &mut Registry, name: String): bool {
        let len = vector::length(&reg.sponsors);
        let mut i: u64 = 0;
        while (i < len) {
            if (*vector::borrow(&reg.sponsors, i) == name) {
                let _ = vector::remove<String>(&mut reg.sponsors, i);
                // drop local `name`
                true;
                return true
            };
            i = i + 1
        };
        // drop local `name`
        true;
        false
    }

    public entry fun append_max(reg: &mut Registry, v: u64) {
        vector::push_back(&mut reg.max_stickers, v);
    }

    public entry fun remove_max_at(reg: &mut Registry, idx: u64): bool {
        let len = vector::length(&reg.max_stickers);
        if (idx >= len) return false;
        let _ = vector::remove<u64>(&mut reg.max_stickers, idx);
        true
    }

    public fun sponsors(reg: &Registry): &vector<String> { &reg.sponsors }
    public fun max_list(reg: &Registry): &vector<u64> { &reg.max_stickers }

    /// =========================
    ///        INVENTORY
    /// =========================

    entry fun new_inventory(ctx: &mut TxContext) {
        let inv = Inventory {
            id: object::new(ctx),
            owner: txc::sender(ctx),
            items: vector::empty<Sticker>(),
        };
        transfer::transfer(inv, txc::sender(ctx));
    }

    /// Read-only getter
    public fun get(inv: &Inventory): &vector<Sticker> { &inv.items }

    /// ---- SAFE entry helpers (enforce owner) ----

    /// Append into your own inventory.
    entry fun inventory_append(inv: &mut Inventory, s: Sticker, ctx: &mut TxContext) {
        assert!(txc::sender(ctx) == inv.owner, 0);
        append(inv, s);
    }

   // If you keep the "safe" entry version that transfers back to caller:
    entry fun inventory_remove(inv: &mut Inventory, sid: ID, ctx: &mut TxContext) {
        assert!(txc::sender(ctx) == inv.owner, 1);
        let s = remove(inv, sid);             // same Sticker resource (same UID)
        transfer::transfer(s, txc::sender(ctx));
    }

    /// ---- PURE helpers (no owner checks, no transfers) ----
    /// These are used by `exchange` to move stickers across inventories.

    /// Append a sticker into an inventory (no checks).
    public fun append(inv: &mut Inventory, s: Sticker) {
        vector::push_back(&mut inv.items, s);
    }

    // Remove and return a Sticker by object ID (no owner checks).
    public fun remove(inv: &mut Inventory, sid: ID): Sticker {
        let n: u64 = vector::length(&inv.items);
        let mut i: u64 = 0;
        while (i < n) {
            let st_ref = vector::borrow(&inv.items, i);   // &Sticker
            if (object::id(st_ref) == sid) {
                return vector::remove<Sticker>(&mut inv.items, i);
            };
            i = i + 1
        };
        abort 2
    }



    /// =========================
    ///    MINTING & REWARDS
    /// =========================

    /// Mint a sticker and ADD it directly into the caller's inventory.
/// Requires: the passed inventory belongs to the sender.
entry fun mint_sticker(
    reg: &Registry,
    inv: &mut Inventory,
    sponsor: String,
    collection_id: u64,
    sticker_id: u64,
    ctx: &mut sui::tx_context::TxContext
) {
    // Inventory must belong to the sender
    assert!(sui::tx_context::sender(ctx) == inv.owner, 10);

    // Validate (sponsor exists at some index i) AND (sticker_id <= max_stickers[i])
    let mut ok = false;
    let len = vector::length(&reg.sponsors);
    let mut i: u64 = 0;
    while (i < len) {
        let sponsor_ref = vector::borrow(&reg.sponsors, i);
        let max_ref = vector::borrow(&reg.max_stickers, i);
        if (*sponsor_ref == sponsor && *max_ref >= sticker_id) {
            ok = true;
            break
        };
        i = i + 1
    };
    assert!(ok, 3);

    // Mint the Sticker resource and move it INTO the inventory
    let s = Sticker {
        id: sui::object::new(ctx),
        sponsor,
        collection_id,
        sticker_id,
    };
    // use your no-check helper so we don’t double-check owner here
    append(inv, s);
    }

    entry fun mint_reward(
        sponsor: String,
        collection_id: u64,
        description: String,
        ctx: &mut TxContext
    ) {
        let r = Reward {
            id: object::new(ctx),
            sponsor,
            collection_id,
            description,
        };
        transfer::transfer(r, txc::sender(ctx))
    }

    // =========================
    //         EXCHANGE
    // =========================
    
    // Remove the sticker from one inventory, and add it to the other (both ways).
    // Uses ONLY the inventory’s `remove` and `append` functions.
    // Exchange: strictly remove from one inventory and add to the other (both ways).
    entry fun exchange(
        inv1: &mut Inventory,
        inv2: &mut Inventory,
        s1_id: ID,   // sticker currently in inv1
        s2_id: ID,   // sticker currently in inv2
        _ctx: &mut TxContext
    ) {
        // Move the exact same Sticker objects (same UID) across inventories
        let a = remove(inv1, s1_id);
        let b = remove(inv2, s2_id);

        // Business rule: must be different collections
        assert!(a.collection_id != b.collection_id, 4);

        append(inv1, b);
        append(inv2, a);
    }
}

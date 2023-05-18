import { Collection, Db, MongoClient } from "mongodb";

export default class DB {
    client: MongoClient;
    db: Db;
    usercollection: Collection;
    accesscollection: Collection;
    constructor(url: string) {
        this.client = new MongoClient(url);
        this.db = this.client.db("one_bbn");
        this.usercollection = this.db.collection("users");
        this.accesscollection = this.db.collection("@bbn/hosting/access");
    }

    async connect() {
        await this.client.connect();
    }

    async finduser(id: string) {
        const user = await this.usercollection.findOne({
            "authentication.type": "oauth",
            "authentication.provider": "discord",
            "authentication.id": id
        })

        if (!user) return null;
        return user._id;
    }

    async getCoins(id: string) {
        const user = await this.finduser(id);
        if (!user) return null;
        const access = await this.accesscollection.findOne({
            owner: user
        });
        if (!access) return null;
        return access.coins;
    }

    async setCoins(id: string, coins: number) {
        // check if user exists
        const user = await this.finduser(id);
        if (!user) return null;
        // update user
        return await this.accesscollection.updateOne({
            owner: user
        }, {
            $set: {
                coins: coins
            }
        });
    }

    async addCoins(id: string, coins: number) {
        // check if user exists
        const user = await this.finduser(id);
        if (!user) return null;
        // update user
        return await this.accesscollection.updateOne({
            owner: user
        }, {
            $inc: {
                coins: coins
            }
        });
    }

    async removeCoins(id: string, coins: number) {
        // check if user exists
        const user = await this.finduser(id);
        if (!user) return null;
        // update user
        return await this.accesscollection.updateOne({
            owner: user
        }, {
            $inc: {
                coins: -coins
            }
        });
    }

    async getLastDaily(id: string) {
        // check if user exists
        const user = await this.finduser(id);
        if (!user) return null;
        const access = await this.accesscollection.findOne({
            owner: user
        });
        if (!access) return null;
        return access.lastDaily;
    }

    async setLastDaily(id: string, lastDaily: number) {
        const user = await this.finduser(id);
        if (!user) return null;
        return await this.accesscollection.updateOne({
            owner: user
        }, {
            $set: {
                lastDaily: lastDaily
            }
        });
    }

}

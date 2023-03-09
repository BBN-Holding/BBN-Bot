import sqlite3 from "sqlite3";

export default class DB {
    db: sqlite3.Database;
    constructor() {
        this.db = new sqlite3.Database("database.sqlite");
    }

    async connect() {
        return new Promise<void>((resolve, reject) => {
            this.db.serialize(() => resolve());
        });
    }

    async get(query: string, params: any[] = []) {
        return new Promise<any>((resolve, reject) => {
            this.db.get(query, params, (err, row) => {
                if (err) {
                    console.log(err);
                }
                resolve(row);
            });
        });
    }

    async all(query: string, params: any[] = []) {
        return new Promise<any[]>((resolve, reject) => {
            this.db.all(query, params, (err, rows) => {
                if (err) {
                    console.log(err);
                }
                resolve(rows);
            });
        });
    }

    async run(query: string, params: any[] = []) {
        return new Promise<void>((resolve, reject) => {
            this.db.run(query, params, (err) => {
                if (err) {
                    console.log(err);
                }
                resolve();
            });
        });
    }

    async getCoins(id: string) {
        const res = await this.get("SELECT * FROM keyv WHERE key = ?", [ "keyv:coins-" + id ]);
        if (!res) return null;
        return JSON.parse(res.value).value;
    }

    async setCoins(id: string, coins: number) {
        // check if user exists
        const res = await this.get("SELECT * FROM keyv WHERE key = ?", [ "keyv:coins-" + id ]);
        if (!res) {
            // create user
            return await this.run("INSERT INTO keyv (key, value) VALUES (?, ?)", [ "keyv:coins-" + id, JSON.stringify({ value: coins }) ]);
        } else {
            // update user
            return await this.run("UPDATE keyv SET value = ? WHERE key = ?", [ JSON.stringify({ value: coins }), "keyv:coins-" + id ]);
        }
    }

    async addCoins(id: string, coins: number) {
        // check if user exists
        const res = await this.get("SELECT * FROM keyv WHERE key = ?", [ "keyv:coins-" + id ]);
        if (!res) {
            // create user
            return await this.run("INSERT INTO keyv (key, value) VALUES (?, ?)", [ "keyv:coins-" + id, JSON.stringify({ value: coins }) ]);
        } else {
            // update user
            return await this.run("UPDATE keyv SET value = ? WHERE key = ?", [ JSON.stringify({ value: JSON.parse(res.value).value + coins }), "keyv:coins-" + id ]);
        }
    }

    async removeCoins(id: string, coins: number) {
        // check if user exists
        const res = await this.get("SELECT * FROM keyv WHERE key = ?", [ "keyv:coins-" + id ]);
        if (!res) {
            // create user
            return await this.run("INSERT INTO keyv (key, value) VALUES (?, ?)", [ "keyv:coins-" + id, JSON.stringify({ value: 0 }) ]);
        } else {
            // update user
            return await this.run("UPDATE keyv SET value = ? WHERE key = ?", [ JSON.stringify({ value: JSON.parse(res.value).value - coins }), "keyv:coins-" + id ]);
        }
    }

    async getLastDaily(id: string) {
        const res = await this.get("SELECT * FROM keyv WHERE key = ?", [ "keyv:lastDaily-" + id ]);
        if (!res) return null;
        return JSON.parse(res.value).value;
    }

    async setLastDaily(id: string, lastDaily: number) {
        // check if user exists
        const res = await this.get("SELECT * FROM keyv WHERE key = ?", [ "keyv:lastDaily-" + id ]);
        if (!res) {
            // create user
            return await this.run("INSERT INTO keyv (key, value) VALUES (?, ?)", [ "keyv:lastDaily-" + id, JSON.stringify({ value: lastDaily }) ]);
        } else {
            // update user
            return await this.run("UPDATE keyv SET value = ? WHERE key = ?", [ JSON.stringify({ value: lastDaily }), "keyv:lastDaily-" + id ]);
        }
    }

}

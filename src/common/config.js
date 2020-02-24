const DefaultConfig = {
  development: {
    baseURI: "http://127.0.0.1/api",
    wsPath: "/channel"
  },
  production: {
    baseURI: "/api",
    wsPath: "/channel"
  }
}

let config = DefaultConfig.development
if (process.env.NODE_ENV === "production") {
  config = DefaultConfig.production
}

const nickname = "Rake Yang"
const baseURI = config.baseURI
const wsPath = config.wsPath
export {
  baseURI,
  wsPath,
  nickname
}
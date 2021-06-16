// swift-tools-version:5.3

import PackageDescription

let package = Package(
	name: "CanaryBackend",
	products: [
		.executable(name: "CanaryBackend", targets: ["CanaryBackend"])
	],
	dependencies: [
		.package(name: "PerfectHTTPServer", url: "https://github.com/PerfectlySoft/Perfect-HTTPServer.git", from: "3.0.0"),
        .package(name: "PerfectWebSockets", url: "https://github.com/PerfectlySoft/Perfect-WebSockets.git", from: "3.0.0"),
        .package(url: "https://github.com/apple/swift-argument-parser", from: "0.4.0"),
        .package(url: "https://github.com/onevcat/Rainbow", .upToNextMajor(from: "4.0.0")),
        .package(url: "https://github.com/BinaryParadise/SwiftyJSON.git", .branch("master")),
        .package(name: "PerfectSQLite", url: "https://github.com/PerfectlySoft/Perfect-SQLite.git", from: "4.0.0"),
        .package(name: "CanaryProto", path: "../common/CanaryProto"),
	],
	targets: [
        .target(
            name: "CanaryBackend",
            dependencies: [
				"PerfectHTTPServer",
				"PerfectWebSockets",
                "Networking",
                "Rainbow",
                "SwiftyJSON",
                "CanaryProto",
                "PerfectSQLite",
                .product(name: "ArgumentParser", package: "swift-argument-parser")
			]
        ),
        .target(
			name: "Networking",
			dependencies: ["PerfectHTTPServer"]
		)
	]
)
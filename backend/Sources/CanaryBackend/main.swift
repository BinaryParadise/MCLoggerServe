//
//  main.swift
//  PerfectTemplate
//
//  Created by Kyle Jessup on 2015-11-05.
//	Copyright (C) 2015 PerfectlySoft, Inc.
//
//===----------------------------------------------------------------------===//
//
// This source file is part of the Perfect.org open source project
//
// Copyright (c) 2015 - 2016 PerfectlySoft Inc. and the Perfect project authors
// Licensed under Apache License v2.0
//
// See http://perfect.org/licensing.html for license information
//
//===----------------------------------------------------------------------===//
//

import PerfectHTTP
import PerfectHTTPServer
import Networking

print(CommandLine.arguments)
ServerArgument.main()

// An example request handler.
// This 'handler' function can be referenced directly in the configuration below.
func handler(request: HTTPRequest, response: HTTPResponse) {
    // Respond with a simple message.
    
}

let _ = HomeController()
// Configure one server which:
//    * Serves the hello world message at <host>:<port>/
//    * Serves static files out of the "./webroot"
//        directory (which must be located in the current working directory).
//    * Performs content compression on outgoing data when appropriate.
routes.add(method: .get, uri: "/**",
           handler: StaticFileHandler(documentRoot: "./Resources.bundle", allowResponseFilters: true).handleRequest)

let _ = WebSocketController()
let _ = ProjectController()

try? HTTPServer.launch(name: conf.name,
                       port: conf.port,
                      routes: routes,
                      requestFilters: [
                        (ContentFilter(), .high)
                      ],
                      responseFilters: [
                        (PerfectHTTPServer.HTTPFilter.contentCompression(data: [:]), HTTPFilterPriority.high)
                      ]).wait()
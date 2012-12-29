# server

A multi-user nREPL server. Proof-of-concept.

## Usage

1. Clone it
2. visit directory and "lein repl"
3. On the server computer: 
```clojure
(use 'server.core)
(new-object-server <port-number>)
```

3. On the client computer:
```clojure
(use 'lamina.core 'aleph.object)
(def ch (object-client {:host <hostname> :port <port-number>}))
(receive-all @ch #(println %))
(enqueue @ch <username>)
```

4. How to send stuff:
```clojure
(enqueue @ch {:op :eval :code "<code-here>"})
```

5. See the results

## License

Copyright © 2012 Daniel Ziltener

Distributed under the Eclipse Public License, the same as Clojure.

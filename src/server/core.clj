(ns server.core
  (:require [clojure.tools.nrepl :as repl]
            [clojure.tools.nrepl.server :as replserver])
  (:use lamina.core aleph.http aleph.object))

(def servers (atom {}))

(def handler2
  (fn handler [ch handshake port]
    (receive ch
             (fn [prompt]
               (let
                   [conn (repl/connect :port (+ port 10))]
                 (let [nrepl (repl/client conn 1000)]
                   (receive-all
                    ch
                    (fn [incoming]
                      (let [broadcast-ch (:broadcast-channel (@servers port))]
                        (println incoming)
                        (enqueue broadcast-ch (str prompt "> " (:code incoming)))
                        (enqueue broadcast-ch (repl/message nrepl incoming)))))
                   (siphon (:broadcast-channel (@servers port)) ch)))))))

(defn new-object-server
  [port]
  (do
    (swap!
     servers
     (fn [serv]
       (assoc serv port {:nrepl-server (replserver/start-server :port (+ port 10))
                         :broadcast-channel (channel)})))
    (future (start-object-server
             (fn [ch handshake]
               (handler2 ch handshake port))
             {:port port}))))

(defn close-server
  [port]
  (do
    (replserver/stop-server (:nrepl-server (@servers port)))
    (close (:broadcast-channel (@servers port)))
    (swap! servers #(dissoc % port))))
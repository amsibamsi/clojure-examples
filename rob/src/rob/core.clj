(ns rob.core
  (:import [org.jivesoftware.smack ConnectionConfiguration
                                   MessageListener
                                   ChatManagerListener
                                   ChatManager]
           [org.jivesoftware.smack.tcp XMPPTCPConnection]
           [org.jivesoftware.smack.packet Message])
  (:require [umgebung.core :as u]
            [clojure.string :as s]
            [clojure.tools.logging :as l]
            [clj-http.client :as h]
            [clojure.string :as string])
  (:gen-class))

(def cfg
  (u/cfg [:rob-service
          :rob-server
          :rob-port
          :rob-username
          :rob-password
          :rob-truststore
          :rob-youtube-key]))

(defn print-cfg
  "print configuration settings."
  []
  (l/info "service is"
          (cfg :rob-service))
  (l/info "server is"
          (cfg :rob-server))
  (l/info "port is"
          (cfg :rob-port))
  (l/info "user is"
          (cfg :rob-username))
  (l/info "password is"
          (if (empty? (cfg :rob-password))
            "not set"
            "set"))
  (l/info "truststore is"
          (cfg :rob-truststore))
  (l/info "youtube key is"
          (if (empty? (cfg :rob-youtube-key))
            "not set"
            "set")))

(defn set-truststore
  "set ssl truststore."
  [filename]
  (when (not (nil? filename))
    (System/setProperty "javax.net.ssl.trustStore"
                        filename)))

(defn new-connection
  "return a new server connection."
  [server port service]
  (XMPPTCPConnection. (ConnectionConfiguration. server
                                                port
                                                service)))

(defn login
  "login to the server."
  [connection user password resource]
  (.connect connection)
  (.login connection
          user
          password
          resource))

(defn reply-youtube-info
  "lookup information for youtube video and send it to the chat."
  [chat id]
  (let [json (h/get (str "https://www.googleapis.com/youtube/v3/videos?id="
                         id
                         "&part=snippet,topicDetails"
                         "&key="
                         (cfg :rob-youtube-key))
                    {:as :json})
        info ((nth ((json :body) :items) 0) :snippet)
        msg (str "youtube "
                 id
                 ": "
                 (info :title)
                 " - "
                 (info :description))]
    (.sendMessage chat
                  (doto (Message.)
                    (.setBody msg)))))

(defn handle-message
  "handle a message in a chat."
  [message chat]
  (let [body (.getBody message)]
    (if (not (nil? body))
      (do
        (l/info "message:"
                body)
        (let [id (last (re-find #".*youtu\.be/(.*)"
                                body))]
          (if id
            (reply-youtube-info chat
                                id)))))))

(defn new-msg-listener
  "return a new message listener."
  []
  (proxy [MessageListener]
    []
    (processMessage [chat message]
      (handle-message message chat))))

(defn new-chat-listener
  "return a new chat listener."
  []
  (proxy [ChatManagerListener]
    []
    (chatCreated [chat local?]
      (.addMessageListener chat
                           (new-msg-listener)))))

(defn chat-manager
  "get chat manager for connection."
  [connection]
  (ChatManager/getInstanceFor connection))

(defn listen
  "register listener to wait for an incoming message."
  [connection]
  (.addChatListener (chat-manager connection)
                    (new-chat-listener)))

(defn -main
  "start it."
  []
  (print-cfg)
  (set-truststore (cfg :rob-truststore))
  (doto (new-connection (cfg :rob-server)
                        (Integer/parseInt (cfg :rob-port))
                        (cfg :rob-service))
    (login (cfg :rob-username)
           (cfg :rob-password)
           "test")
    (listen))
  (loop []
    (Thread/sleep Long/MAX_VALUE)))

;(defn new-chat
;  "create a new chat."
;  [connection listener buddy]
;  (.createChat (ChatManager/getInstanceFor connection)
;               buddy
;               listener))

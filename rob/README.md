# rob

a jabber bot that can do the following:

- receive messages and if a message is of the form <code>youtu.be/&lt;id&gt;</code> reply the title and description of the youtube video in the chat.

yes, that's all it can do.

## setup

the following environment variables have to be defined:

- ROB_SERVICE jabber resource name
- ROB_SERVER jabber server name
- ROB_PORT jabber server port
- ROB_USERNAME jabber login username
- ROB_PASSWORD jabber login password
- ROB_TRUSTSTORE optional ssl truststore file
- ROB_YOUTUBE_KEY youtube api key

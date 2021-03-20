# im

## how to send msg to another people(check IMWebSocket.java)
1. login -> get token , like token = 123, and ,refresh_token = 234
2. send to one person, whose user id is , for example '2'

    url: ws://localhost:8080/api/im/123/234
    msg(json):
    {"toUserId":2,"topicId":0,"isGroupMessage":false,"msgContent":"hello word"}

3. send to group(represented by topic Id, like 7)

    url: ws://localhost:8080/api/im/123/234
    msg(json):
    {"topicId":7,"isGroupMessage":true,"msgContent":"hello word"}


all detail about msg refer IMMessage.java
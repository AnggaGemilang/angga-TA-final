var connected_flag = 0
var mqtt
var reconnectTimeout = 2000
var host="test.mosquitto.org"
var port=8080
var sub_topic="arnesys/#"

function onConnectionLost(){
    console.log("connection lost")
    connected_flag = 0
}

function onFailure(message) {
    console.log("Failed")
    setTimeout(MQTTconnect, reconnectTimeout)
}

function onConnected(recon,url){
    console.log(" in onConnected " + reconn)
}

function MQTTconnect() {
    // console.log("connecting to "+ host +":"+ port)
    var x = Math.floor(Math.random() * 10000)
    var cname = "controlform-" + x
    mqtt = new Paho.MQTT.Client(host,port,cname)
    mqtt.onConnectionLost = onConnectionLost
    mqtt.onMessageArrived = onMessageArrived
    mqtt.connect({
        timeout: 5,
        onSuccess: onConnect,
        onFailure: onFailure
    })
    return false
}

function onConnect() {
    connected_flag = 1
    console.log("on Connect "+connected_flag)
    mqtt.subscribe(sub_topic)
}

function sub_topics(){
    document.getElementById("messages").innerHTML =""
    if (connected_flag==0){
        out_msg = "<b>Not Connected so can't subscribe</b>"
        console.log(out_msg)
        return false
    }

    var stopic= document.forms["subs"]["Stopic"].value
    console.log("Subscribing to topic ="+stopic)
    mqtt.subscribe(stopic)
    return false
}

function send_message(msg,topic){
    if (connected_flag==0){
        out_msg="<b>Not Connected so can't send</b>"
        console.log(out_msg)
        document.getElementById("messages").innerHTML = out_msg
        return false
    }

    var value=msg.value
    console.log("value= "+value)
    console.log("topic= "+topic)
    message = new Paho.MQTT.Message(value)
    message.destinationName = "house/"+topic

    mqtt.send(message)
    return false
}

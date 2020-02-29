package com.kincony.kbox.net

import android.util.Log

class BoxCommand {
    companion object {
        /**
         * control relay ON/OFF
         *
         * feedback：RELAY-SET-255,x(channel),x（1byte action 0/1）,OK/ERROR
         * 【Such as Turn ON relay2， RELAY-SET-255,2,1】
         * 【 Turn OFF relay2， RELAY-SET-255,2,0】
         */
        fun set(number: Int, action: Int): String {
            return "RELAY-SET-255,$number,$action"
        }

        /**
         * Read relay state
         *
         * feedback：RELAY-READ-255,x(channel),x(1byte state 0/1),OK/ERROR
         *【Such as Read state of relay3， RELAY-READ-255,3】
         */
        fun read(number: Int): String {
            var command = "RELAY-READ-255,$number"
            return command
        }

        /**
         * Read input port for sensor
         *
         * feedback：RELAY-GET_INPUT-255,x(1byte state),OK/ERROR
         */
        fun get(): String {
            return "RELAY-GET_INPUT-255"
        }


        /**
         * ALARM trig：
         *
         * feedback：RELAY-ALARM-x(1byte channel),OK/ERROR
         */
        fun alarm(number: Int): String {
            return "RELAY-ALARM-$number"
        }

        /**
         * KC868-H32 channel relay controller：
         *  Send：RELAY-SET_ALL-255,D3,D2,D1,D0
         *  feedback：RELAY-SET_ALL-255,D3,D2,D1,D0,OK/ERROR
         *  【such as Turn ON relay 29-32 ， RELAY-SET_ALL-255,240,0,0,0】
         *  【 Turn ON relay 21-24 , RELAY-SET_ALL-255,0,240,0,0】
         *
         * KC868-H16 channel relay controller：
         *  Send：RELAY-SET_ALL-255,D1,D0
         *  feedback：RELAY-SET_ALL-255,D1,D0,OK/ERROR
         *
         * KC868-H2/4/8 channel relay controller：
         *  Send：RELAY-SET_ALL-255,D0
         *  feedback：RELAY-SET_ALL-255,D0,OK/ERROR
         * 【Such as Turn ON relay 1 3 5 7 ， RELAY-SET_ALL-255,85】
         * 【Such as Turn ON relay 1 2 3 4 ， RELAY-SET_ALL-255,15】
         */
        fun setAll(number: IntArray): String {
            // :TODO
            return "RELAY-SET_ALL-255,"
        }

        /**
         * feedback：
         *  KC868-H32：RELAY-STATE-255,D3,D2,D1,D0,OK/ERROR
         *  KC868-H16：RELAY-STATE-255,D1,D0,OK/ERROR
         *  KC868-H8/4/2：RELAY-STATE-255,D0,OK/ERROR
         */
        fun readAll(): String {
            return "RELAY-STATE-255"
        }

    }
}
/*
 * Copyright (c) 2016-2026 Salvatore D'Angelo
 * Licensed under the MIT License. See the LICENSE file in the project root.
 */
package org.code4projects.framework.impl

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class AccelerometerHandler(context: Context) : SensorEventListener {
    private var accelX = 0f
    private var accelY = 0f
    private var accelZ = 0f

    init {
        val manager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if (manager.getSensorList(Sensor.TYPE_ACCELEROMETER).isNotEmpty()) {
            val accelerometer = manager.getSensorList(Sensor.TYPE_ACCELEROMETER)[0]
            manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // nothing to do here
    }

    override fun onSensorChanged(event: SensorEvent) {
        accelX = event.values[0]
        accelY = event.values[1]
        accelZ = event.values[2]
    }

    fun getAccelX(): Float = accelX

    fun getAccelY(): Float = accelY

    fun getAccelZ(): Float = accelZ
}

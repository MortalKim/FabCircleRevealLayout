/*
 * Copyright (C) 2015 Tomás Ruiz-López.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hankim.layout

class Point {
    var x: Float
    var y: Float
    var control0X = 0f
    protected var control0Y = 0f
    var control1X = 0f
    protected var control1Y = 0f

    constructor(control0X: Float, control0Y: Float, control1X: Float, control1Y: Float, x: Float, y: Float) {
        this.control0X = control0X
        this.control0Y = control0Y
        this.control1X = control1X
        this.control1Y = control1Y
        this.x = x
        this.y = y
    }

    constructor(x: Float, y: Float) {
        this.x = x
        this.y = y
    }
}
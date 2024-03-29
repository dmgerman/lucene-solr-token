begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.geo
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|SloppyMath
import|;
end_import

begin_comment
comment|/** Draws shapes on the earth surface and renders using the very cool http://www.webglearth.org.  *  * Just instantiate this class, add the things you want plotted, and call {@link #finish} to get the  * resulting HTML that you should save and load with a browser. */
end_comment

begin_class
DECL|class|EarthDebugger
specifier|public
class|class
name|EarthDebugger
block|{
DECL|field|b
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
DECL|field|nextShape
specifier|private
name|int
name|nextShape
decl_stmt|;
DECL|field|finished
specifier|private
name|boolean
name|finished
decl_stmt|;
DECL|method|EarthDebugger
specifier|public
name|EarthDebugger
parameter_list|()
block|{
name|b
operator|.
name|append
argument_list|(
literal|"<!DOCTYPE HTML>\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"<html>\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"<head>\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"<script src=\"http://www.webglearth.com/v2/api.js\"></script>\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"<script>\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"      function initialize() {\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"        var earth = new WE.map('earth_div');\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|EarthDebugger
specifier|public
name|EarthDebugger
parameter_list|(
name|double
name|centerLat
parameter_list|,
name|double
name|centerLon
parameter_list|,
name|double
name|altitudeMeters
parameter_list|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"<!DOCTYPE HTML>\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"<html>\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"<head>\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"<script src=\"http://www.webglearth.com/v2/api.js\"></script>\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"<script>\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"      function initialize() {\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"        var earth = new WE.map('earth_div', {center: ["
operator|+
name|centerLat
operator|+
literal|", "
operator|+
name|centerLon
operator|+
literal|"], altitude: "
operator|+
name|altitudeMeters
operator|+
literal|"});\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|addPolygon
specifier|public
name|void
name|addPolygon
parameter_list|(
name|Polygon
name|poly
parameter_list|)
block|{
name|addPolygon
argument_list|(
name|poly
argument_list|,
literal|"#00ff00"
argument_list|)
expr_stmt|;
block|}
DECL|method|addPolygon
specifier|public
name|void
name|addPolygon
parameter_list|(
name|Polygon
name|poly
parameter_list|,
name|String
name|color
parameter_list|)
block|{
name|String
name|name
init|=
literal|"poly"
operator|+
name|nextShape
decl_stmt|;
name|nextShape
operator|++
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"        var "
operator|+
name|name
operator|+
literal|" = WE.polygon([\n"
argument_list|)
expr_stmt|;
name|double
index|[]
name|polyLats
init|=
name|poly
operator|.
name|getPolyLats
argument_list|()
decl_stmt|;
name|double
index|[]
name|polyLons
init|=
name|poly
operator|.
name|getPolyLons
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|polyLats
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"          ["
operator|+
name|polyLats
index|[
name|i
index|]
operator|+
literal|", "
operator|+
name|polyLons
index|[
name|i
index|]
operator|+
literal|"],\n"
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|"        ], {color: '"
operator|+
name|color
operator|+
literal|"', fillColor: \"#000000\", fillOpacity: 0.0001});\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"        "
operator|+
name|name
operator|+
literal|".addTo(earth);\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|Polygon
name|hole
range|:
name|poly
operator|.
name|getHoles
argument_list|()
control|)
block|{
name|addPolygon
argument_list|(
name|hole
argument_list|,
literal|"#ffffff"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|MAX_KM_PER_STEP
specifier|private
specifier|static
name|double
name|MAX_KM_PER_STEP
init|=
literal|100.0
decl_stmt|;
comment|// Web GL earth connects dots by tunneling under the earth, so we approximate a great circle by sampling it, to minimize how deep in the
comment|// earth each segment tunnels:
DECL|method|getStepCount
specifier|private
name|int
name|getStepCount
parameter_list|(
name|double
name|minLat
parameter_list|,
name|double
name|maxLat
parameter_list|,
name|double
name|minLon
parameter_list|,
name|double
name|maxLon
parameter_list|)
block|{
name|double
name|distanceMeters
init|=
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|minLat
argument_list|,
name|minLon
argument_list|,
name|maxLat
argument_list|,
name|maxLon
argument_list|)
decl_stmt|;
return|return
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
operator|(
name|int
operator|)
name|Math
operator|.
name|round
argument_list|(
operator|(
name|distanceMeters
operator|/
literal|1000.0
operator|)
operator|/
name|MAX_KM_PER_STEP
argument_list|)
argument_list|)
return|;
block|}
comment|// first point is inclusive, last point is exclusive!
DECL|method|drawSegment
specifier|private
name|void
name|drawSegment
parameter_list|(
name|double
name|minLat
parameter_list|,
name|double
name|maxLat
parameter_list|,
name|double
name|minLon
parameter_list|,
name|double
name|maxLon
parameter_list|)
block|{
name|int
name|steps
init|=
name|getStepCount
argument_list|(
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|,
name|maxLon
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|steps
condition|;
name|i
operator|++
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"          ["
operator|+
operator|(
name|minLat
operator|+
operator|(
name|maxLat
operator|-
name|minLat
operator|)
operator|*
name|i
operator|/
name|steps
operator|)
operator|+
literal|", "
operator|+
operator|(
name|minLon
operator|+
operator|(
name|maxLon
operator|-
name|minLon
operator|)
operator|*
name|i
operator|/
name|steps
operator|)
operator|+
literal|"],\n"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addRect
specifier|public
name|void
name|addRect
parameter_list|(
name|double
name|minLat
parameter_list|,
name|double
name|maxLat
parameter_list|,
name|double
name|minLon
parameter_list|,
name|double
name|maxLon
parameter_list|)
block|{
name|addRect
argument_list|(
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|,
name|maxLon
argument_list|,
literal|"#ff0000"
argument_list|)
expr_stmt|;
block|}
DECL|method|addRect
specifier|public
name|void
name|addRect
parameter_list|(
name|double
name|minLat
parameter_list|,
name|double
name|maxLat
parameter_list|,
name|double
name|minLon
parameter_list|,
name|double
name|maxLon
parameter_list|,
name|String
name|color
parameter_list|)
block|{
name|String
name|name
init|=
literal|"rect"
operator|+
name|nextShape
decl_stmt|;
name|nextShape
operator|++
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"        // lat: "
operator|+
name|minLat
operator|+
literal|" TO "
operator|+
name|maxLat
operator|+
literal|"; lon: "
operator|+
name|minLon
operator|+
literal|" TO "
operator|+
name|maxLon
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"        var "
operator|+
name|name
operator|+
literal|" = WE.polygon([\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"          // min -> max lat, min lon\n"
argument_list|)
expr_stmt|;
name|drawSegment
argument_list|(
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|,
name|minLon
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"          // max lat, min -> max lon\n"
argument_list|)
expr_stmt|;
name|drawSegment
argument_list|(
name|maxLat
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|,
name|maxLon
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"          // max -> min lat, max lon\n"
argument_list|)
expr_stmt|;
name|drawSegment
argument_list|(
name|maxLat
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|,
name|maxLon
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"          // min lat, max -> min lon\n"
argument_list|)
expr_stmt|;
name|drawSegment
argument_list|(
name|minLat
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|,
name|minLon
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"          // min lat, min lon\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"          ["
operator|+
name|minLat
operator|+
literal|", "
operator|+
name|minLon
operator|+
literal|"]\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"        ], {color: \""
operator|+
name|color
operator|+
literal|"\", fillColor: \""
operator|+
name|color
operator|+
literal|"\"});\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"        "
operator|+
name|name
operator|+
literal|".addTo(earth);\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Draws a line a fixed latitude, spanning the min/max longitude */
DECL|method|addLatLine
specifier|public
name|void
name|addLatLine
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|minLon
parameter_list|,
name|double
name|maxLon
parameter_list|)
block|{
name|String
name|name
init|=
literal|"latline"
operator|+
name|nextShape
decl_stmt|;
name|nextShape
operator|++
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"        var "
operator|+
name|name
operator|+
literal|" = WE.polygon([\n"
argument_list|)
expr_stmt|;
name|double
name|lon
decl_stmt|;
name|int
name|steps
init|=
name|getStepCount
argument_list|(
name|lat
argument_list|,
name|minLon
argument_list|,
name|lat
argument_list|,
name|maxLon
argument_list|)
decl_stmt|;
for|for
control|(
name|lon
operator|=
name|minLon
init|;
name|lon
operator|<=
name|maxLon
condition|;
name|lon
operator|+=
operator|(
name|maxLon
operator|-
name|minLon
operator|)
operator|/
name|steps
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"          ["
operator|+
name|lat
operator|+
literal|", "
operator|+
name|lon
operator|+
literal|"],\n"
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|"          ["
operator|+
name|lat
operator|+
literal|", "
operator|+
name|maxLon
operator|+
literal|"],\n"
argument_list|)
expr_stmt|;
name|lon
operator|-=
operator|(
name|maxLon
operator|-
name|minLon
operator|)
operator|/
name|steps
expr_stmt|;
for|for
control|(
init|;
name|lon
operator|>=
name|minLon
condition|;
name|lon
operator|-=
operator|(
name|maxLon
operator|-
name|minLon
operator|)
operator|/
name|steps
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"          ["
operator|+
name|lat
operator|+
literal|", "
operator|+
name|lon
operator|+
literal|"],\n"
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|"        ], {color: \"#ff0000\", fillColor: \"#ffffff\", opacity: 1, fillOpacity: 0.0001});\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"        "
operator|+
name|name
operator|+
literal|".addTo(earth);\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Draws a line a fixed longitude, spanning the min/max latitude */
DECL|method|addLonLine
specifier|public
name|void
name|addLonLine
parameter_list|(
name|double
name|minLat
parameter_list|,
name|double
name|maxLat
parameter_list|,
name|double
name|lon
parameter_list|)
block|{
name|String
name|name
init|=
literal|"lonline"
operator|+
name|nextShape
decl_stmt|;
name|nextShape
operator|++
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"        var "
operator|+
name|name
operator|+
literal|" = WE.polygon([\n"
argument_list|)
expr_stmt|;
name|double
name|lat
decl_stmt|;
name|int
name|steps
init|=
name|getStepCount
argument_list|(
name|minLat
argument_list|,
name|lon
argument_list|,
name|maxLat
argument_list|,
name|lon
argument_list|)
decl_stmt|;
for|for
control|(
name|lat
operator|=
name|minLat
init|;
name|lat
operator|<=
name|maxLat
condition|;
name|lat
operator|+=
operator|(
name|maxLat
operator|-
name|minLat
operator|)
operator|/
name|steps
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"          ["
operator|+
name|lat
operator|+
literal|", "
operator|+
name|lon
operator|+
literal|"],\n"
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|"          ["
operator|+
name|maxLat
operator|+
literal|", "
operator|+
name|lon
operator|+
literal|"],\n"
argument_list|)
expr_stmt|;
name|lat
operator|-=
operator|(
name|maxLat
operator|-
name|minLat
operator|)
operator|/
literal|36
expr_stmt|;
for|for
control|(
init|;
name|lat
operator|>=
name|minLat
condition|;
name|lat
operator|-=
operator|(
name|maxLat
operator|-
name|minLat
operator|)
operator|/
name|steps
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"          ["
operator|+
name|lat
operator|+
literal|", "
operator|+
name|lon
operator|+
literal|"],\n"
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|"        ], {color: \"#ff0000\", fillColor: \"#ffffff\", opacity: 1, fillOpacity: 0.0001});\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"        "
operator|+
name|name
operator|+
literal|".addTo(earth);\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|addPoint
specifier|public
name|void
name|addPoint
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"        WE.marker(["
operator|+
name|lat
operator|+
literal|", "
operator|+
name|lon
operator|+
literal|"]).addTo(earth);\n"
argument_list|)
expr_stmt|;
block|}
DECL|method|addCircle
specifier|public
name|void
name|addCircle
parameter_list|(
name|double
name|centerLat
parameter_list|,
name|double
name|centerLon
parameter_list|,
name|double
name|radiusMeters
parameter_list|,
name|boolean
name|alsoAddBBox
parameter_list|)
block|{
name|addPoint
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|)
expr_stmt|;
name|String
name|name
init|=
literal|"circle"
operator|+
name|nextShape
decl_stmt|;
name|nextShape
operator|++
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"        var "
operator|+
name|name
operator|+
literal|" = WE.polygon([\n"
argument_list|)
expr_stmt|;
name|inverseHaversin
argument_list|(
name|b
argument_list|,
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|radiusMeters
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"        ], {color: '#00ff00', fillColor: \"#000000\", fillOpacity: 0.0001 });\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"        "
operator|+
name|name
operator|+
literal|".addTo(earth);\n"
argument_list|)
expr_stmt|;
if|if
condition|(
name|alsoAddBBox
condition|)
block|{
name|Rectangle
name|box
init|=
name|Rectangle
operator|.
name|fromPointDistance
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|radiusMeters
argument_list|)
decl_stmt|;
name|addRect
argument_list|(
name|box
operator|.
name|minLat
argument_list|,
name|box
operator|.
name|maxLat
argument_list|,
name|box
operator|.
name|minLon
argument_list|,
name|box
operator|.
name|maxLon
argument_list|)
expr_stmt|;
name|addLatLine
argument_list|(
name|Rectangle
operator|.
name|axisLat
argument_list|(
name|centerLat
argument_list|,
name|radiusMeters
argument_list|)
argument_list|,
name|box
operator|.
name|minLon
argument_list|,
name|box
operator|.
name|maxLon
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|finish
specifier|public
name|String
name|finish
parameter_list|()
block|{
if|if
condition|(
name|finished
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"already finished"
argument_list|)
throw|;
block|}
name|finished
operator|=
literal|true
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"        WE.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',{\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"          attribution: 'Â© OpenStreetMap contributors'\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"        }).addTo(earth);\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"      }\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"</script>\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"<style>\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"      html, body{padding: 0; margin: 0;}\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"      #earth_div{top: 0; right: 0; bottom: 0; left: 0; position: absolute !important;}\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"</style>\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"<title>WebGL Earth API: Hello World</title>\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"</head>\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"<body onload=\"initialize()\">\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"<div id=\"earth_div\"></div>\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"</body>\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"</html>\n"
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|inverseHaversin
specifier|private
specifier|static
name|void
name|inverseHaversin
parameter_list|(
name|StringBuilder
name|b
parameter_list|,
name|double
name|centerLat
parameter_list|,
name|double
name|centerLon
parameter_list|,
name|double
name|radiusMeters
parameter_list|)
block|{
name|double
name|angle
init|=
literal|0
decl_stmt|;
name|int
name|steps
init|=
literal|100
decl_stmt|;
name|newAngle
label|:
while|while
condition|(
name|angle
operator|<
literal|360
condition|)
block|{
name|double
name|x
init|=
name|Math
operator|.
name|cos
argument_list|(
name|SloppyMath
operator|.
name|toRadians
argument_list|(
name|angle
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|y
init|=
name|Math
operator|.
name|sin
argument_list|(
name|SloppyMath
operator|.
name|toRadians
argument_list|(
name|angle
argument_list|)
argument_list|)
decl_stmt|;
name|double
name|factor
init|=
literal|2.0
decl_stmt|;
name|double
name|step
init|=
literal|1.0
decl_stmt|;
name|int
name|last
init|=
literal|0
decl_stmt|;
name|double
name|lastDistanceMeters
init|=
literal|0.0
decl_stmt|;
comment|//System.out.println("angle " + angle + " slope=" + slope);
while|while
condition|(
literal|true
condition|)
block|{
name|double
name|lat
init|=
name|wrapLat
argument_list|(
name|centerLat
operator|+
name|y
operator|*
name|factor
argument_list|)
decl_stmt|;
name|double
name|lon
init|=
name|wrapLon
argument_list|(
name|centerLon
operator|+
name|x
operator|*
name|factor
argument_list|)
decl_stmt|;
name|double
name|distanceMeters
init|=
name|SloppyMath
operator|.
name|haversinMeters
argument_list|(
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|)
decl_stmt|;
if|if
condition|(
name|last
operator|==
literal|1
operator|&&
name|distanceMeters
operator|<
name|lastDistanceMeters
condition|)
block|{
comment|// For large enough circles, some angles are not possible:
comment|//System.out.println("  done: give up on angle " + angle);
name|angle
operator|+=
literal|360.
operator|/
name|steps
expr_stmt|;
continue|continue
name|newAngle
continue|;
block|}
if|if
condition|(
name|last
operator|==
operator|-
literal|1
operator|&&
name|distanceMeters
operator|>
name|lastDistanceMeters
condition|)
block|{
comment|// For large enough circles, some angles are not possible:
comment|//System.out.println("  done: give up on angle " + angle);
name|angle
operator|+=
literal|360.
operator|/
name|steps
expr_stmt|;
continue|continue
name|newAngle
continue|;
block|}
name|lastDistanceMeters
operator|=
name|distanceMeters
expr_stmt|;
comment|//System.out.println("  iter lat=" + lat + " lon=" + lon + " distance=" + distanceMeters + " vs " + radiusMeters);
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|distanceMeters
operator|-
name|radiusMeters
argument_list|)
operator|<
literal|0.1
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"          ["
operator|+
name|lat
operator|+
literal|", "
operator|+
name|lon
operator|+
literal|"],\n"
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|distanceMeters
operator|>
name|radiusMeters
condition|)
block|{
comment|// too big
comment|//System.out.println("    smaller");
name|factor
operator|-=
name|step
expr_stmt|;
if|if
condition|(
name|last
operator|==
literal|1
condition|)
block|{
comment|//System.out.println("      half-step");
name|step
operator|/=
literal|2.0
expr_stmt|;
block|}
name|last
operator|=
operator|-
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|distanceMeters
operator|<
name|radiusMeters
condition|)
block|{
comment|// too small
comment|//System.out.println("    bigger");
name|factor
operator|+=
name|step
expr_stmt|;
if|if
condition|(
name|last
operator|==
operator|-
literal|1
condition|)
block|{
comment|//System.out.println("      half-step");
name|step
operator|/=
literal|2.0
expr_stmt|;
block|}
name|last
operator|=
literal|1
expr_stmt|;
block|}
block|}
name|angle
operator|+=
literal|360.
operator|/
name|steps
expr_stmt|;
block|}
block|}
comment|// craziness for plotting stuff :)
DECL|method|wrapLat
specifier|private
specifier|static
name|double
name|wrapLat
parameter_list|(
name|double
name|lat
parameter_list|)
block|{
comment|//System.out.println("wrapLat " + lat);
if|if
condition|(
name|lat
operator|>
literal|90
condition|)
block|{
comment|//System.out.println("  " + (180 - lat));
return|return
literal|180
operator|-
name|lat
return|;
block|}
elseif|else
if|if
condition|(
name|lat
operator|<
operator|-
literal|90
condition|)
block|{
comment|//System.out.println("  " + (-180 - lat));
return|return
operator|-
literal|180
operator|-
name|lat
return|;
block|}
else|else
block|{
comment|//System.out.println("  " + lat);
return|return
name|lat
return|;
block|}
block|}
DECL|method|wrapLon
specifier|private
specifier|static
name|double
name|wrapLon
parameter_list|(
name|double
name|lon
parameter_list|)
block|{
comment|//System.out.println("wrapLon " + lon);
if|if
condition|(
name|lon
operator|>
literal|180
condition|)
block|{
comment|//System.out.println("  " + (lon - 360));
return|return
name|lon
operator|-
literal|360
return|;
block|}
elseif|else
if|if
condition|(
name|lon
operator|<
operator|-
literal|180
condition|)
block|{
comment|//System.out.println("  " + (lon + 360));
return|return
name|lon
operator|+
literal|360
return|;
block|}
else|else
block|{
comment|//System.out.println("  " + lon);
return|return
name|lon
return|;
block|}
block|}
block|}
end_class

end_unit


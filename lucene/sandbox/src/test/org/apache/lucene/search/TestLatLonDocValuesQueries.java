begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|document
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|LatLonDocValuesField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
operator|.
name|BaseGeoPointTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
operator|.
name|GeoEncodingUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
operator|.
name|Polygon
import|;
end_import

begin_class
DECL|class|TestLatLonDocValuesQueries
specifier|public
class|class
name|TestLatLonDocValuesQueries
extends|extends
name|BaseGeoPointTestCase
block|{
annotation|@
name|Override
DECL|method|supportsPolygons
specifier|protected
name|boolean
name|supportsPolygons
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|addPointToDoc
specifier|protected
name|void
name|addPointToDoc
parameter_list|(
name|String
name|field
parameter_list|,
name|Document
name|doc
parameter_list|,
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|LatLonDocValuesField
argument_list|(
name|field
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newRectQuery
specifier|protected
name|Query
name|newRectQuery
parameter_list|(
name|String
name|field
parameter_list|,
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
return|return
name|LatLonDocValuesField
operator|.
name|newBoxQuery
argument_list|(
name|field
argument_list|,
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|,
name|maxLon
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newDistanceQuery
specifier|protected
name|Query
name|newDistanceQuery
parameter_list|(
name|String
name|field
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
return|return
name|LatLonDocValuesField
operator|.
name|newDistanceQuery
argument_list|(
name|field
argument_list|,
name|centerLat
argument_list|,
name|centerLon
argument_list|,
name|radiusMeters
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newPolygonQuery
specifier|protected
name|Query
name|newPolygonQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|Polygon
modifier|...
name|polygons
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|quantizeLat
specifier|protected
name|double
name|quantizeLat
parameter_list|(
name|double
name|latRaw
parameter_list|)
block|{
return|return
name|GeoEncodingUtils
operator|.
name|decodeLatitude
argument_list|(
name|GeoEncodingUtils
operator|.
name|encodeLatitude
argument_list|(
name|latRaw
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|quantizeLon
specifier|protected
name|double
name|quantizeLon
parameter_list|(
name|double
name|lonRaw
parameter_list|)
block|{
return|return
name|GeoEncodingUtils
operator|.
name|decodeLongitude
argument_list|(
name|GeoEncodingUtils
operator|.
name|encodeLongitude
argument_list|(
name|lonRaw
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

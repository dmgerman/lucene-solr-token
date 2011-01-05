begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search.function.distance
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
operator|.
name|distance
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|DistanceUtils
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
name|spatial
operator|.
name|geohash
operator|.
name|GeoHashUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrTestCaseJ4
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|DistanceFunctionTest
specifier|public
class|class
name|DistanceFunctionTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema12.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHaversine
specifier|public
name|void
name|testHaversine
parameter_list|()
throws|throws
name|Exception
block|{
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"x_td"
argument_list|,
literal|"0"
argument_list|,
literal|"y_td"
argument_list|,
literal|"0"
argument_list|,
literal|"gh_s"
argument_list|,
name|GeoHashUtils
operator|.
name|encode
argument_list|(
literal|32.7693246
argument_list|,
operator|-
literal|79.9289094
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"x_td"
argument_list|,
literal|"0"
argument_list|,
literal|"y_td"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|Math
operator|.
name|PI
operator|/
literal|2
argument_list|)
argument_list|,
literal|"gh_s"
argument_list|,
name|GeoHashUtils
operator|.
name|encode
argument_list|(
literal|32.7693246
argument_list|,
operator|-
literal|78.9289094
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"x_td"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|Math
operator|.
name|PI
operator|/
literal|2
argument_list|)
argument_list|,
literal|"y_td"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|Math
operator|.
name|PI
operator|/
literal|2
argument_list|)
argument_list|,
literal|"gh_s"
argument_list|,
name|GeoHashUtils
operator|.
name|encode
argument_list|(
literal|32.7693246
argument_list|,
operator|-
literal|80.9289094
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"x_td"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|Math
operator|.
name|PI
operator|/
literal|4
argument_list|)
argument_list|,
literal|"y_td"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|Math
operator|.
name|PI
operator|/
literal|4
argument_list|)
argument_list|,
literal|"gh_s"
argument_list|,
name|GeoHashUtils
operator|.
name|encode
argument_list|(
literal|32.7693246
argument_list|,
operator|-
literal|81.9289094
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"x_td"
argument_list|,
literal|"45.0"
argument_list|,
literal|"y_td"
argument_list|,
literal|"45.0"
argument_list|,
literal|"gh_s"
argument_list|,
name|GeoHashUtils
operator|.
name|encode
argument_list|(
literal|32.7693246
argument_list|,
operator|-
literal|81.9289094
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
literal|"point_hash"
argument_list|,
literal|"32.5, -79.0"
argument_list|,
literal|"point"
argument_list|,
literal|"32.5, -79.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|,
literal|"point_hash"
argument_list|,
literal|"32.6, -78.0"
argument_list|,
literal|"point"
argument_list|,
literal|"32.6, -78.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|//Get the haversine distance between the point 0,0 and the docs above assuming a radius of 1
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}hsin(1, false, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"//float[@name='score']='0.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}hsin(1, false, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:2"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
call|(
name|float
call|)
argument_list|(
name|Math
operator|.
name|PI
operator|/
literal|2
argument_list|)
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}hsin(1, false, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:3"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
call|(
name|float
call|)
argument_list|(
name|Math
operator|.
name|PI
operator|/
literal|2
argument_list|)
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}hsin(1, false, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:4"
argument_list|)
argument_list|,
literal|"//float[@name='score']='1.0471976'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}hsin(1, true, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:5"
argument_list|)
argument_list|,
literal|"//float[@name='score']='1.0471976'"
argument_list|)
expr_stmt|;
comment|//SOLR-2114
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}hsin(6371.009, true, point, vector(0, 0))"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:6"
argument_list|)
argument_list|,
literal|"//float[@name='score']='8977.814'"
argument_list|)
expr_stmt|;
comment|//Geo Hash Haversine
comment|//Can verify here: http://www.movable-type.co.uk/scripts/latlong.html, but they use a slightly different radius for the earth, so just be close
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}ghhsin("
operator|+
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
operator|+
literal|", gh_s, \""
operator|+
name|GeoHashUtils
operator|.
name|encode
argument_list|(
literal|32
argument_list|,
operator|-
literal|79
argument_list|)
operator|+
literal|"\",)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"//float[@name='score']='122.171875'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id,point_hash,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}recip(ghhsin("
operator|+
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
operator|+
literal|", point_hash, \""
operator|+
name|GeoHashUtils
operator|.
name|encode
argument_list|(
literal|32
argument_list|,
operator|-
literal|79
argument_list|)
operator|+
literal|"\"), 1, 1, 0)"
argument_list|)
argument_list|,
literal|"//*[@numFound='7']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='6']"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.='7']"
comment|//all the rest don't matter
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}ghhsin("
operator|+
name|DistanceUtils
operator|.
name|EARTH_MEAN_RADIUS_KM
operator|+
literal|", gh_s, geohash(32, -79))"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"//float[@name='score']='122.171875'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLatLon
specifier|public
name|void
name|testLatLon
parameter_list|()
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"100"
argument_list|,
literal|"store"
argument_list|,
literal|"1,2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"func"
argument_list|,
literal|"q"
argument_list|,
literal|"geodist(1,2,3,4)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:100"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|)
argument_list|,
literal|"/response/docs/[0]/score==314.40338"
argument_list|)
expr_stmt|;
comment|// throw in some decimal points
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"func"
argument_list|,
literal|"q"
argument_list|,
literal|"geodist(1.0,2,3,4.0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:100"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|)
argument_list|,
literal|"/response/docs/[0]/score==314.40338"
argument_list|)
expr_stmt|;
comment|// default to reading pt
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"func"
argument_list|,
literal|"q"
argument_list|,
literal|"geodist(1,2)"
argument_list|,
literal|"pt"
argument_list|,
literal|"3,4"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:100"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|)
argument_list|,
literal|"/response/docs/[0]/score==314.40338"
argument_list|)
expr_stmt|;
comment|// default to reading pt first
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"func"
argument_list|,
literal|"q"
argument_list|,
literal|"geodist(1,2)"
argument_list|,
literal|"pt"
argument_list|,
literal|"3,4"
argument_list|,
literal|"sfield"
argument_list|,
literal|"store"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:100"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|)
argument_list|,
literal|"/response/docs/[0]/score==314.40338"
argument_list|)
expr_stmt|;
comment|// if pt missing, use sfield
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"func"
argument_list|,
literal|"q"
argument_list|,
literal|"geodist(3,4)"
argument_list|,
literal|"sfield"
argument_list|,
literal|"store"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:100"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|)
argument_list|,
literal|"/response/docs/[0]/score==314.40338"
argument_list|)
expr_stmt|;
comment|// read both pt and sfield
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"func"
argument_list|,
literal|"q"
argument_list|,
literal|"geodist()"
argument_list|,
literal|"pt"
argument_list|,
literal|"3,4"
argument_list|,
literal|"sfield"
argument_list|,
literal|"store"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:100"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|)
argument_list|,
literal|"/response/docs/[0]/score==314.40338"
argument_list|)
expr_stmt|;
comment|// param substitution
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"defType"
argument_list|,
literal|"func"
argument_list|,
literal|"q"
argument_list|,
literal|"geodist($a,$b)"
argument_list|,
literal|"a"
argument_list|,
literal|"3,4"
argument_list|,
literal|"b"
argument_list|,
literal|"store"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:100"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|)
argument_list|,
literal|"/response/docs/[0]/score==314.40338"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testVector
specifier|public
name|void
name|testVector
parameter_list|()
throws|throws
name|Exception
block|{
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"x_td"
argument_list|,
literal|"0"
argument_list|,
literal|"y_td"
argument_list|,
literal|"0"
argument_list|,
literal|"z_td"
argument_list|,
literal|"0"
argument_list|,
literal|"w_td"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"x_td"
argument_list|,
literal|"0"
argument_list|,
literal|"y_td"
argument_list|,
literal|"1"
argument_list|,
literal|"z_td"
argument_list|,
literal|"0"
argument_list|,
literal|"w_td"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"x_td"
argument_list|,
literal|"1"
argument_list|,
literal|"y_td"
argument_list|,
literal|"1"
argument_list|,
literal|"z_td"
argument_list|,
literal|"1"
argument_list|,
literal|"w_td"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"x_td"
argument_list|,
literal|"1"
argument_list|,
literal|"y_td"
argument_list|,
literal|"0"
argument_list|,
literal|"z_td"
argument_list|,
literal|"0"
argument_list|,
literal|"w_td"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"x_td"
argument_list|,
literal|"2.3"
argument_list|,
literal|"y_td"
argument_list|,
literal|"5.5"
argument_list|,
literal|"z_td"
argument_list|,
literal|"7.9"
argument_list|,
literal|"w_td"
argument_list|,
literal|"-2.4"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
literal|"point"
argument_list|,
literal|"1.0,0.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|,
literal|"point"
argument_list|,
literal|"5.5,10.9"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|//two dimensions, notice how we only pass in 4 value sources
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"//float[@name='score']='0.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:2"
argument_list|)
argument_list|,
literal|"//float[@name='score']='1.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:3"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
literal|2.0f
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:4"
argument_list|)
argument_list|,
literal|"//float[@name='score']='1.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:5"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
call|(
name|float
call|)
argument_list|(
literal|2.3
operator|*
literal|2.3
operator|+
literal|5.5
operator|*
literal|5.5
argument_list|)
operator|+
literal|"'"
argument_list|)
expr_stmt|;
comment|//three dimensions, notice how we pass in 6 value sources
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, z_td, 0, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"//float[@name='score']='0.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, z_td, 0, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:2"
argument_list|)
argument_list|,
literal|"//float[@name='score']='1.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, z_td, 0, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:3"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
literal|3.0f
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, z_td, 0, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:4"
argument_list|)
argument_list|,
literal|"//float[@name='score']='1.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, z_td, 0, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:5"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
call|(
name|float
call|)
argument_list|(
literal|2.3
operator|*
literal|2.3
operator|+
literal|5.5
operator|*
literal|5.5
operator|+
literal|7.9
operator|*
literal|7.9
argument_list|)
operator|+
literal|"'"
argument_list|)
expr_stmt|;
comment|//four dimensions, notice how we pass in 8 value sources
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, z_td, w_td, 0, 0, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"//float[@name='score']='0.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, z_td, w_td, 0, 0, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:2"
argument_list|)
argument_list|,
literal|"//float[@name='score']='1.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, z_td, w_td, 0, 0, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:3"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
literal|4.0f
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, z_td, w_td, 0, 0, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:4"
argument_list|)
argument_list|,
literal|"//float[@name='score']='1.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, z_td, w_td, 0, 0, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:5"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
call|(
name|float
call|)
argument_list|(
literal|2.3
operator|*
literal|2.3
operator|+
literal|5.5
operator|*
literal|5.5
operator|+
literal|7.9
operator|*
literal|7.9
operator|+
literal|2.4
operator|*
literal|2.4
argument_list|)
operator|+
literal|"'"
argument_list|)
expr_stmt|;
comment|//Pass in imbalanced list, throw exception
try|try
block|{
name|ignoreException
argument_list|(
literal|"Illegal number of sources"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}sqedist(x_td, y_td, z_td, w_td, 0, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"//float[@name='score']='0.0'"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"should throw an exception"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Throwable
name|cause
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|cause
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cause
operator|instanceof
name|SolrException
argument_list|)
expr_stmt|;
block|}
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
comment|//do one test of Euclidean
comment|//two dimensions, notice how we only pass in 4 value sources
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}dist(2, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"//float[@name='score']='0.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}dist(2, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:2"
argument_list|)
argument_list|,
literal|"//float[@name='score']='1.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}dist(2, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:3"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
operator|(
name|float
operator|)
name|Math
operator|.
name|sqrt
argument_list|(
literal|2.0
argument_list|)
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}dist(2, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:4"
argument_list|)
argument_list|,
literal|"//float[@name='score']='1.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}dist(2, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:5"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
operator|(
name|float
operator|)
name|Math
operator|.
name|sqrt
argument_list|(
operator|(
literal|2.3
operator|*
literal|2.3
operator|+
literal|5.5
operator|*
literal|5.5
operator|)
argument_list|)
operator|+
literal|"'"
argument_list|)
expr_stmt|;
comment|//do one test of Manhattan
comment|//two dimensions, notice how we only pass in 4 value sources
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}dist(1, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"//float[@name='score']='0.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}dist(1, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:2"
argument_list|)
argument_list|,
literal|"//float[@name='score']='1.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}dist(1, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:3"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
operator|(
name|float
operator|)
literal|2.0
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}dist(1, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:4"
argument_list|)
argument_list|,
literal|"//float[@name='score']='1.0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}dist(1, x_td, y_td, 0, 0)"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:5"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
call|(
name|float
call|)
argument_list|(
literal|2.3
operator|+
literal|5.5
argument_list|)
operator|+
literal|"'"
argument_list|)
expr_stmt|;
comment|//Do point tests:
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}dist(1, vector(x_td, y_td), vector(0, 0))"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:5"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
call|(
name|float
call|)
argument_list|(
literal|2.3
operator|+
literal|5.5
argument_list|)
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}dist(1, point, vector(0, 0))"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:6"
argument_list|)
argument_list|,
literal|"//float[@name='score']='"
operator|+
literal|1.0f
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


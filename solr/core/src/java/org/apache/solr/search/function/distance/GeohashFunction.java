begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReaderContext
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|spatial4j
operator|.
name|io
operator|.
name|GeohashUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Takes in a latitude and longitude ValueSource and produces a GeoHash.  *<p>  * Ex: geohash(lat, lon)  *  *<p>  * Note, there is no reciprocal function for this.  **/
end_comment

begin_class
DECL|class|GeohashFunction
specifier|public
class|class
name|GeohashFunction
extends|extends
name|ValueSource
block|{
DECL|field|lat
DECL|field|lon
specifier|protected
name|ValueSource
name|lat
decl_stmt|,
name|lon
decl_stmt|;
DECL|method|GeohashFunction
specifier|public
name|GeohashFunction
parameter_list|(
name|ValueSource
name|lat
parameter_list|,
name|ValueSource
name|lon
parameter_list|)
block|{
name|this
operator|.
name|lat
operator|=
name|lat
expr_stmt|;
name|this
operator|.
name|lon
operator|=
name|lon
expr_stmt|;
block|}
DECL|method|name
specifier|protected
name|String
name|name
parameter_list|()
block|{
return|return
literal|"geohash"
return|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|public
name|FunctionValues
name|getValues
parameter_list|(
name|Map
name|context
parameter_list|,
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FunctionValues
name|latDV
init|=
name|lat
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|readerContext
argument_list|)
decl_stmt|;
specifier|final
name|FunctionValues
name|lonDV
init|=
name|lon
operator|.
name|getValues
argument_list|(
name|context
argument_list|,
name|readerContext
argument_list|)
decl_stmt|;
return|return
operator|new
name|FunctionValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|GeohashUtils
operator|.
name|encodeLatLon
argument_list|(
name|latDV
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
argument_list|,
name|lonDV
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|name
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|latDV
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|lonDV
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|GeohashFunction
operator|)
condition|)
return|return
literal|false
return|;
name|GeohashFunction
name|that
init|=
operator|(
name|GeohashFunction
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|lat
operator|.
name|equals
argument_list|(
name|that
operator|.
name|lat
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|lon
operator|.
name|equals
argument_list|(
name|that
operator|.
name|lon
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|lat
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|29
operator|*
name|result
operator|-
name|lon
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|name
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|lat
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|lon
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit


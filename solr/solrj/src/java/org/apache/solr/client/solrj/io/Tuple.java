begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.io
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  *  A simple abstraction of a record containing key/value pairs.  *  Convenience methods are provided for returning single and multiValue String, Long and Double values.  *  Note that ints and floats are treated as longs and doubles respectively.  * **/
end_comment

begin_class
DECL|class|Tuple
specifier|public
class|class
name|Tuple
implements|implements
name|Cloneable
block|{
comment|/**    *  When EOF field is true the Tuple marks the end of the stream.    *  The EOF Tuple will not contain a record from the stream, but it may contain    *  metrics/aggregates gathered by underlying streams.    * */
DECL|field|EOF
specifier|public
name|boolean
name|EOF
decl_stmt|;
DECL|field|EXCEPTION
specifier|public
name|boolean
name|EXCEPTION
decl_stmt|;
DECL|field|fields
specifier|public
name|Map
name|fields
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
DECL|method|Tuple
specifier|public
name|Tuple
parameter_list|(
name|Map
name|fields
parameter_list|)
block|{
if|if
condition|(
name|fields
operator|.
name|containsKey
argument_list|(
literal|"EOF"
argument_list|)
condition|)
block|{
name|EOF
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|fields
operator|.
name|containsKey
argument_list|(
literal|"EXCEPTION"
argument_list|)
condition|)
block|{
name|EXCEPTION
operator|=
literal|true
expr_stmt|;
block|}
name|this
operator|.
name|fields
operator|.
name|putAll
argument_list|(
name|fields
argument_list|)
expr_stmt|;
block|}
DECL|method|get
specifier|public
name|Object
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|this
operator|.
name|fields
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
DECL|method|put
specifier|public
name|void
name|put
parameter_list|(
name|Object
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|getString
specifier|public
name|String
name|getString
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|this
operator|.
name|fields
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getException
specifier|public
name|String
name|getException
parameter_list|()
block|{
return|return
operator|(
name|String
operator|)
name|this
operator|.
name|fields
operator|.
name|get
argument_list|(
literal|"EXCEPTION"
argument_list|)
return|;
block|}
DECL|method|getLong
specifier|public
name|Long
name|getLong
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|Object
name|o
init|=
name|this
operator|.
name|fields
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|Long
condition|)
block|{
return|return
operator|(
name|Long
operator|)
name|o
return|;
block|}
else|else
block|{
comment|//Attempt to parse the long
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|method|getDouble
specifier|public
name|Double
name|getDouble
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|Object
name|o
init|=
name|this
operator|.
name|fields
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|Double
condition|)
block|{
return|return
operator|(
name|Double
operator|)
name|o
return|;
block|}
else|else
block|{
comment|//Attempt to parse the double
return|return
name|Double
operator|.
name|parseDouble
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|method|getStrings
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getStrings
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|this
operator|.
name|fields
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
DECL|method|getLongs
specifier|public
name|List
argument_list|<
name|Long
argument_list|>
name|getLongs
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
operator|(
name|List
argument_list|<
name|Long
argument_list|>
operator|)
name|this
operator|.
name|fields
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
DECL|method|getDoubles
specifier|public
name|List
argument_list|<
name|Double
argument_list|>
name|getDoubles
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
operator|(
name|List
argument_list|<
name|Double
argument_list|>
operator|)
name|this
operator|.
name|fields
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
DECL|method|getMap
specifier|public
name|Map
name|getMap
parameter_list|()
block|{
return|return
name|this
operator|.
name|fields
return|;
block|}
DECL|method|getMaps
specifier|public
name|List
argument_list|<
name|Map
argument_list|>
name|getMaps
parameter_list|()
block|{
return|return
operator|(
name|List
argument_list|<
name|Map
argument_list|>
operator|)
name|this
operator|.
name|fields
operator|.
name|get
argument_list|(
literal|"_MAPS_"
argument_list|)
return|;
block|}
DECL|method|setMaps
specifier|public
name|void
name|setMaps
parameter_list|(
name|List
argument_list|<
name|Map
argument_list|>
name|maps
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|.
name|put
argument_list|(
literal|"_MAPS_"
argument_list|,
name|maps
argument_list|)
expr_stmt|;
block|}
DECL|method|getMetrics
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|>
name|getMetrics
parameter_list|()
block|{
return|return
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|>
operator|)
name|this
operator|.
name|fields
operator|.
name|get
argument_list|(
literal|"_METRICS_"
argument_list|)
return|;
block|}
DECL|method|setMetrics
specifier|public
name|void
name|setMetrics
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|>
name|metrics
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|.
name|put
argument_list|(
literal|"_METRICS_"
argument_list|,
name|metrics
argument_list|)
expr_stmt|;
block|}
DECL|method|clone
specifier|public
name|Tuple
name|clone
parameter_list|()
block|{
name|HashMap
name|m
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|m
operator|.
name|putAll
argument_list|(
name|fields
argument_list|)
expr_stmt|;
name|Tuple
name|clone
init|=
operator|new
name|Tuple
argument_list|(
name|m
argument_list|)
decl_stmt|;
return|return
name|clone
return|;
block|}
block|}
end_class

end_unit


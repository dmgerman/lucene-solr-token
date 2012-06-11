begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.logging
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|logging
package|;
end_package

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
name|util
operator|.
name|SimpleOrderedMap
import|;
end_import

begin_comment
comment|/**  * Wrapper class for Logger implementaions  */
end_comment

begin_class
DECL|class|LoggerInfo
specifier|public
specifier|abstract
class|class
name|LoggerInfo
implements|implements
name|Comparable
argument_list|<
name|LoggerInfo
argument_list|>
block|{
DECL|field|ROOT_NAME
specifier|public
specifier|static
specifier|final
name|String
name|ROOT_NAME
init|=
literal|"root"
decl_stmt|;
DECL|field|name
specifier|protected
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|level
specifier|protected
name|String
name|level
decl_stmt|;
DECL|method|LoggerInfo
specifier|public
name|LoggerInfo
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|getLevel
specifier|public
name|String
name|getLevel
parameter_list|()
block|{
return|return
name|level
return|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|isSet
specifier|public
specifier|abstract
name|boolean
name|isSet
parameter_list|()
function_decl|;
DECL|method|getInfo
specifier|public
name|SimpleOrderedMap
argument_list|<
name|?
argument_list|>
name|getInfo
parameter_list|()
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|info
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"level"
argument_list|,
name|getLevel
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|add
argument_list|(
literal|"set"
argument_list|,
name|isSet
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|LoggerInfo
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|equals
argument_list|(
name|other
argument_list|)
condition|)
return|return
literal|0
return|;
name|String
name|tN
init|=
name|this
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|oN
init|=
name|other
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|ROOT_NAME
operator|.
name|equals
argument_list|(
name|tN
argument_list|)
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|ROOT_NAME
operator|.
name|equals
argument_list|(
name|oN
argument_list|)
condition|)
return|return
literal|1
return|;
return|return
name|tN
operator|.
name|compareTo
argument_list|(
name|oN
argument_list|)
return|;
block|}
block|}
end_class

end_unit


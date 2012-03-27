begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ServiceLoader
import|;
end_import

begin_comment
comment|/**  * Helper class for loading named SPIs from classpath (e.g. Codec, PostingsFormat).  * @lucene.internal  */
end_comment

begin_class
DECL|class|NamedSPILoader
specifier|public
specifier|final
class|class
name|NamedSPILoader
parameter_list|<
name|S
extends|extends
name|NamedSPILoader
operator|.
name|NamedSPI
parameter_list|>
implements|implements
name|Iterable
argument_list|<
name|S
argument_list|>
block|{
DECL|field|services
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|S
argument_list|>
name|services
decl_stmt|;
comment|/** This field is a hack for LuceneTestCase to get access    * to the modifiable map (to work around bugs in IBM J9) */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
annotation|@
name|Deprecated
comment|// Hackidy-HÃ¤ck-Hack for bugs in IBM J9 ServiceLoader
DECL|field|modifiableServices
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|S
argument_list|>
name|modifiableServices
decl_stmt|;
DECL|field|clazz
specifier|private
specifier|final
name|Class
argument_list|<
name|S
argument_list|>
name|clazz
decl_stmt|;
DECL|method|NamedSPILoader
specifier|public
name|NamedSPILoader
parameter_list|(
name|Class
argument_list|<
name|S
argument_list|>
name|clazz
parameter_list|)
block|{
name|this
operator|.
name|clazz
operator|=
name|clazz
expr_stmt|;
specifier|final
name|ServiceLoader
argument_list|<
name|S
argument_list|>
name|loader
init|=
name|ServiceLoader
operator|.
name|load
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
specifier|final
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|S
argument_list|>
name|services
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|S
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|S
name|service
range|:
name|loader
control|)
block|{
specifier|final
name|String
name|name
init|=
name|service
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// only add the first one for each name, later services will be ignored
comment|// this allows to place services before others in classpath to make
comment|// them used instead of others
if|if
condition|(
operator|!
name|services
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|services
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|service
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|modifiableServices
operator|=
name|services
expr_stmt|;
comment|// hack, remove when IBM J9 is fixed!
name|this
operator|.
name|services
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|services
argument_list|)
expr_stmt|;
block|}
DECL|method|lookup
specifier|public
name|S
name|lookup
parameter_list|(
name|String
name|name
parameter_list|)
block|{
specifier|final
name|S
name|service
init|=
name|services
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|service
operator|!=
literal|null
condition|)
return|return
name|service
return|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"A SPI class of type "
operator|+
name|clazz
operator|.
name|getName
argument_list|()
operator|+
literal|" with name '"
operator|+
name|name
operator|+
literal|"' does not exist. "
operator|+
literal|"You need to add the corresponding JAR file supporting this SPI to your classpath."
operator|+
literal|"The current classpath supports the following names: "
operator|+
name|availableServices
argument_list|()
argument_list|)
throw|;
block|}
DECL|method|availableServices
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|availableServices
parameter_list|()
block|{
return|return
name|services
operator|.
name|keySet
argument_list|()
return|;
block|}
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|S
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|services
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|interface|NamedSPI
specifier|public
specifier|static
interface|interface
name|NamedSPI
block|{
DECL|method|getName
name|String
name|getName
parameter_list|()
function_decl|;
block|}
block|}
end_class

end_unit


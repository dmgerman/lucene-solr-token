begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.common.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

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
name|LinkedHashMap
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
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|Utils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|CreateMode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|Stat
import|;
end_import

begin_comment
comment|/**  * Interact with solr cluster properties  *  * Note that all methods on this class make calls to ZK on every invocation.  For  * read-only eventually-consistent uses, clients should instead call  * {@link ZkStateReader#getClusterProperty(String, Object)}  */
end_comment

begin_class
DECL|class|ClusterProperties
specifier|public
class|class
name|ClusterProperties
block|{
DECL|field|client
specifier|private
specifier|final
name|SolrZkClient
name|client
decl_stmt|;
comment|/**    * Creates a ClusterProperties object using a provided SolrZkClient    */
DECL|method|ClusterProperties
specifier|public
name|ClusterProperties
parameter_list|(
name|SolrZkClient
name|client
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
block|}
comment|/**    * Read the value of a cluster property, returning a default if it is not set    * @param key           the property name    * @param defaultValue  the default value    * @param<T>           the type of the property    * @return the property value    * @throws IOException if there is an error reading the value from the cluster    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getClusterProperty
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|getClusterProperty
parameter_list|(
name|String
name|key
parameter_list|,
name|T
name|defaultValue
parameter_list|)
throws|throws
name|IOException
block|{
name|T
name|value
init|=
operator|(
name|T
operator|)
name|getClusterProperties
argument_list|()
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
return|return
name|defaultValue
return|;
return|return
name|value
return|;
block|}
comment|/**    * Return the cluster properties    * @throws IOException if there is an error reading properties from the cluster    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getClusterProperties
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getClusterProperties
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
return|return
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|Utils
operator|.
name|fromJSON
argument_list|(
name|client
operator|.
name|getData
argument_list|(
name|ZkStateReader
operator|.
name|CLUSTER_PROPS
argument_list|,
literal|null
argument_list|,
operator|new
name|Stat
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoNodeException
name|e
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|KeeperException
decl||
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error reading cluster property"
argument_list|,
name|SolrZkClient
operator|.
name|checkInterrupted
argument_list|(
name|e
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|/**    * This method sets a cluster property.    *    * @param propertyName  The property name to be set.    * @param propertyValue The value of the property.    * @throws IOException if there is an error writing data to the cluster    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|setClusterProperty
specifier|public
name|void
name|setClusterProperty
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|String
name|propertyValue
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|ZkStateReader
operator|.
name|KNOWN_CLUSTER_PROPS
operator|.
name|contains
argument_list|(
name|propertyName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Not a known cluster property "
operator|+
name|propertyName
argument_list|)
throw|;
block|}
for|for
control|(
init|;
condition|;
control|)
block|{
name|Stat
name|s
init|=
operator|new
name|Stat
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|client
operator|.
name|exists
argument_list|(
name|ZkStateReader
operator|.
name|CLUSTER_PROPS
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|Map
name|properties
init|=
operator|(
name|Map
operator|)
name|Utils
operator|.
name|fromJSON
argument_list|(
name|client
operator|.
name|getData
argument_list|(
name|ZkStateReader
operator|.
name|CLUSTER_PROPS
argument_list|,
literal|null
argument_list|,
name|s
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|propertyValue
operator|==
literal|null
condition|)
block|{
comment|//Don't update ZK unless absolutely necessary.
if|if
condition|(
name|properties
operator|.
name|get
argument_list|(
name|propertyName
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|properties
operator|.
name|remove
argument_list|(
name|propertyName
argument_list|)
expr_stmt|;
name|client
operator|.
name|setData
argument_list|(
name|ZkStateReader
operator|.
name|CLUSTER_PROPS
argument_list|,
name|Utils
operator|.
name|toJSON
argument_list|(
name|properties
argument_list|)
argument_list|,
name|s
operator|.
name|getVersion
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//Don't update ZK unless absolutely necessary.
if|if
condition|(
operator|!
name|propertyValue
operator|.
name|equals
argument_list|(
name|properties
operator|.
name|get
argument_list|(
name|propertyName
argument_list|)
argument_list|)
condition|)
block|{
name|properties
operator|.
name|put
argument_list|(
name|propertyName
argument_list|,
name|propertyValue
argument_list|)
expr_stmt|;
name|client
operator|.
name|setData
argument_list|(
name|ZkStateReader
operator|.
name|CLUSTER_PROPS
argument_list|,
name|Utils
operator|.
name|toJSON
argument_list|(
name|properties
argument_list|)
argument_list|,
name|s
operator|.
name|getVersion
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|Map
name|properties
init|=
operator|new
name|LinkedHashMap
argument_list|()
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|propertyName
argument_list|,
name|propertyValue
argument_list|)
expr_stmt|;
name|client
operator|.
name|create
argument_list|(
name|ZkStateReader
operator|.
name|CLUSTER_PROPS
argument_list|,
name|Utils
operator|.
name|toJSON
argument_list|(
name|properties
argument_list|)
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|BadVersionException
decl||
name|KeeperException
operator|.
name|NodeExistsException
name|e
parameter_list|)
block|{
comment|//race condition
continue|continue;
block|}
catch|catch
parameter_list|(
name|InterruptedException
decl||
name|KeeperException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error setting cluster property"
argument_list|,
name|SolrZkClient
operator|.
name|checkInterrupted
argument_list|(
name|e
argument_list|)
argument_list|)
throw|;
block|}
break|break;
block|}
block|}
block|}
end_class

end_unit


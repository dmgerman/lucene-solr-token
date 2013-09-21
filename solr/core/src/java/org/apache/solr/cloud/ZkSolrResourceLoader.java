begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
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
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|Properties
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
name|SolrException
operator|.
name|ErrorCode
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
name|cloud
operator|.
name|ZooKeeperException
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
name|core
operator|.
name|SolrResourceLoader
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

begin_comment
comment|/**  * ResourceLoader that works with ZooKeeper.  *  */
end_comment

begin_class
DECL|class|ZkSolrResourceLoader
specifier|public
class|class
name|ZkSolrResourceLoader
extends|extends
name|SolrResourceLoader
block|{
DECL|field|collectionZkPath
specifier|private
specifier|final
name|String
name|collectionZkPath
decl_stmt|;
DECL|field|zkController
specifier|private
name|ZkController
name|zkController
decl_stmt|;
DECL|method|ZkSolrResourceLoader
specifier|public
name|ZkSolrResourceLoader
parameter_list|(
name|String
name|instanceDir
parameter_list|,
name|String
name|collection
parameter_list|,
name|ZkController
name|zooKeeperController
parameter_list|)
block|{
name|super
argument_list|(
name|instanceDir
argument_list|)
expr_stmt|;
name|this
operator|.
name|zkController
operator|=
name|zooKeeperController
expr_stmt|;
name|collectionZkPath
operator|=
name|ZkController
operator|.
name|CONFIGS_ZKNODE
operator|+
literal|"/"
operator|+
name|collection
expr_stmt|;
block|}
comment|/**    *<p>    * This loader will first attempt to load resources from ZooKeeper, but if not found    * will delegate to the context classloader when possible,    * otherwise it will attempt to resolve resources using any jar files found in    * the "lib/" directory in the specified instance directory.    *<p>    */
DECL|method|ZkSolrResourceLoader
specifier|public
name|ZkSolrResourceLoader
parameter_list|(
name|String
name|instanceDir
parameter_list|,
name|String
name|collection
parameter_list|,
name|ClassLoader
name|parent
parameter_list|,
name|Properties
name|coreProperties
parameter_list|,
name|ZkController
name|zooKeeperController
parameter_list|)
block|{
name|super
argument_list|(
name|instanceDir
argument_list|,
name|parent
argument_list|,
name|coreProperties
argument_list|)
expr_stmt|;
name|this
operator|.
name|zkController
operator|=
name|zooKeeperController
expr_stmt|;
name|collectionZkPath
operator|=
name|ZkController
operator|.
name|CONFIGS_ZKNODE
operator|+
literal|"/"
operator|+
name|collection
expr_stmt|;
block|}
comment|/**    * Opens any resource by its name. By default, this will look in multiple    * locations to load the resource: $configDir/$resource from ZooKeeper.    * It will look for it in any jar    * accessible through the class loader if it cannot be found in ZooKeeper.     * Override this method to customize loading resources.    *     * @return the stream for the named resource    */
annotation|@
name|Override
DECL|method|openResource
specifier|public
name|InputStream
name|openResource
parameter_list|(
name|String
name|resource
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
name|String
name|file
init|=
name|collectionZkPath
operator|+
literal|"/"
operator|+
name|resource
decl_stmt|;
try|try
block|{
if|if
condition|(
name|zkController
operator|.
name|pathExists
argument_list|(
name|file
argument_list|)
condition|)
block|{
name|byte
index|[]
name|bytes
init|=
name|zkController
operator|.
name|getZkClient
argument_list|()
operator|.
name|getData
argument_list|(
name|file
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error opening "
operator|+
name|file
argument_list|,
name|e
argument_list|)
throw|;
block|}
try|try
block|{
comment|// delegate to the class loader (looking into $INSTANCE_DIR/lib jars)
name|is
operator|=
name|classLoader
operator|.
name|getResourceAsStream
argument_list|(
name|resource
operator|.
name|replace
argument_list|(
name|File
operator|.
name|separatorChar
argument_list|,
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error opening "
operator|+
name|resource
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't find resource '"
operator|+
name|resource
operator|+
literal|"' in classpath or '"
operator|+
name|collectionZkPath
operator|+
literal|"', cwd="
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|is
return|;
block|}
annotation|@
name|Override
DECL|method|getConfigDir
specifier|public
name|String
name|getConfigDir
parameter_list|()
block|{
throw|throw
operator|new
name|ZooKeeperException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"ZkSolrResourceLoader does not support getConfigDir() - likely, what you are trying to do is not supported in ZooKeeper mode"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|listConfigDir
specifier|public
name|String
index|[]
name|listConfigDir
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|list
decl_stmt|;
try|try
block|{
name|list
operator|=
name|zkController
operator|.
name|getZkClient
argument_list|()
operator|.
name|getChildren
argument_list|(
name|collectionZkPath
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// Restore the interrupted status
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|""
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ZooKeeperException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|""
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|""
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ZooKeeperException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|""
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|list
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
return|;
block|}
DECL|method|getCollectionZkPath
specifier|public
name|String
name|getCollectionZkPath
parameter_list|()
block|{
return|return
name|collectionZkPath
return|;
block|}
DECL|method|getZkController
specifier|public
name|ZkController
name|getZkController
parameter_list|()
block|{
return|return
name|zkController
return|;
block|}
block|}
end_class

end_unit


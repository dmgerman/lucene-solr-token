begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

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
name|ByteArrayOutputStream
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
name|cloud
operator|.
name|SolrZkClient
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
operator|.
name|NodeExistsException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  *<p>  *  A SolrCloud-friendly extension of {@link SimplePropertiesWriter}.    *  This implementation ignores the "directory" parameter, saving  *  the properties file under /configs/[solrcloud collection name]/  */
end_comment

begin_class
DECL|class|ZKPropertiesWriter
specifier|public
class|class
name|ZKPropertiesWriter
extends|extends
name|SimplePropertiesWriter
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ZKPropertiesWriter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
DECL|field|zkClient
specifier|private
name|SolrZkClient
name|zkClient
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|DataImporter
name|dataImporter
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|dataImporter
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|String
name|collection
init|=
name|dataImporter
operator|.
name|getCore
argument_list|()
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getCollectionName
argument_list|()
decl_stmt|;
name|path
operator|=
literal|"/configs/"
operator|+
name|collection
operator|+
literal|"/"
operator|+
name|filename
expr_stmt|;
name|zkClient
operator|=
name|dataImporter
operator|.
name|getCore
argument_list|()
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkClient
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isWritable
specifier|public
name|boolean
name|isWritable
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|persist
specifier|public
name|void
name|persist
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|propObjs
parameter_list|)
block|{
name|Properties
name|existing
init|=
name|mapToProperties
argument_list|(
name|readIndexerProperties
argument_list|()
argument_list|)
decl_stmt|;
name|existing
operator|.
name|putAll
argument_list|(
name|mapToProperties
argument_list|(
name|propObjs
argument_list|)
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|output
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
try|try
block|{
name|existing
operator|.
name|store
argument_list|(
name|output
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|output
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|zkClient
operator|.
name|exists
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
condition|)
block|{
try|try
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NodeExistsException
name|e
parameter_list|)
block|{}
block|}
name|zkClient
operator|.
name|setData
argument_list|(
name|path
argument_list|,
name|bytes
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
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
name|warn
argument_list|(
literal|"Could not persist properties to "
operator|+
name|path
operator|+
literal|" :"
operator|+
name|e
operator|.
name|getClass
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Could not persist properties to "
operator|+
name|path
operator|+
literal|" :"
operator|+
name|e
operator|.
name|getClass
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|readIndexerProperties
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|readIndexerProperties
parameter_list|()
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|data
init|=
name|zkClient
operator|.
name|getData
argument_list|(
name|path
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|!=
literal|null
condition|)
block|{
name|ByteArrayInputStream
name|input
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|props
operator|.
name|load
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Could not read DIH properties from "
operator|+
name|path
operator|+
literal|" :"
operator|+
name|e
operator|.
name|getClass
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|propertiesToMap
argument_list|(
name|props
argument_list|)
return|;
block|}
block|}
end_class

end_unit


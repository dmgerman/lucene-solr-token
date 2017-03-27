begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|FileVisitResult
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|SimpleFileVisitor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|attribute
operator|.
name|BasicFileAttributes
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
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|client
operator|.
name|solrj
operator|.
name|SolrServerException
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
comment|/**  * Class to hold  ZK upload/download/move common code. With the advent of the upconfig/downconfig/cp/ls/mv commands  * in bin/solr it made sense to keep the individual transfer methods in a central place, so here it is.  */
end_comment

begin_class
DECL|class|ZkMaintenanceUtils
specifier|public
class|class
name|ZkMaintenanceUtils
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
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|ZKNODE_DATA_FILE
specifier|private
specifier|static
specifier|final
name|String
name|ZKNODE_DATA_FILE
init|=
literal|"zknode.data"
decl_stmt|;
DECL|method|ZkMaintenanceUtils
specifier|private
name|ZkMaintenanceUtils
parameter_list|()
block|{}
comment|// don't let it be instantiated, all methods are static.
comment|/**    * Lists a ZNode child and (optionally) the znodes of all the children. No data is dumped.    *    * @param path    The node to remove on Zookeeper    * @param recurse Whether to remove children.    * @throws KeeperException      Could not perform the Zookeeper operation.    * @throws InterruptedException Thread interrupted    * @throws SolrServerException  zookeeper node has children and recurse not specified.    * @return an indented list of the znodes suitable for display    */
DECL|method|listZnode
specifier|public
specifier|static
name|String
name|listZnode
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|String
name|path
parameter_list|,
name|Boolean
name|recurse
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
throws|,
name|SolrServerException
block|{
name|String
name|root
init|=
name|path
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"zk:"
argument_list|)
condition|)
block|{
name|root
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|root
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
operator|==
literal|false
operator|&&
name|root
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|root
operator|=
name|root
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|root
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|recurse
operator|==
literal|false
condition|)
block|{
for|for
control|(
name|String
name|node
range|:
name|zkClient
operator|.
name|getChildren
argument_list|(
name|root
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
control|)
block|{
if|if
condition|(
name|node
operator|.
name|equals
argument_list|(
literal|"zookeeper"
argument_list|)
operator|==
literal|false
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|node
argument_list|)
operator|.
name|append
argument_list|(
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
name|traverseZkTree
argument_list|(
name|zkClient
argument_list|,
name|root
argument_list|,
name|VISIT_ORDER
operator|.
name|VISIT_PRE
argument_list|,
name|znode
lambda|->
block|{
if|if
condition|(
name|znode
operator|.
name|startsWith
argument_list|(
literal|"/zookeeper"
argument_list|)
condition|)
return|return;
comment|// can't do anything with this node!
name|int
name|iPos
init|=
name|znode
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
if|if
condition|(
name|iPos
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|iPos
condition|;
operator|++
name|idx
control|)
name|sb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|znode
operator|.
name|substring
argument_list|(
name|iPos
operator|+
literal|1
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|znode
argument_list|)
operator|.
name|append
argument_list|(
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Copy between local file system and Zookeeper, or from one Zookeeper node to another,    * optionally copying recursively.    *    * @param src     Source to copy from. Both src and dst may be Znodes. However, both may NOT be local    * @param dst     The place to copy the files too. Both src and dst may be Znodes. However both may NOT be local    * @param recurse if the source is a directory, reccursively copy the contents iff this is true.    * @throws SolrServerException  Explanatory exception due to bad params, failed operation, etc.    * @throws KeeperException      Could not perform the Zookeeper operation.    * @throws InterruptedException Thread interrupted    */
DECL|method|zkTransfer
specifier|public
specifier|static
name|void
name|zkTransfer
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|String
name|src
parameter_list|,
name|Boolean
name|srcIsZk
parameter_list|,
name|String
name|dst
parameter_list|,
name|Boolean
name|dstIsZk
parameter_list|,
name|Boolean
name|recurse
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|KeeperException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
if|if
condition|(
name|srcIsZk
operator|==
literal|false
operator|&&
name|dstIsZk
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|SolrServerException
argument_list|(
literal|"One or both of source or destination must specify ZK nodes."
argument_list|)
throw|;
block|}
comment|// Make sure -recurse is specified if the source has children.
if|if
condition|(
name|recurse
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|srcIsZk
condition|)
block|{
if|if
condition|(
name|zkClient
operator|.
name|getChildren
argument_list|(
name|src
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|SolrServerException
argument_list|(
literal|"Zookeeper node "
operator|+
name|src
operator|+
literal|" has children and recurse is false"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|Files
operator|.
name|isDirectory
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|src
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrServerException
argument_list|(
literal|"Local path "
operator|+
name|Paths
operator|.
name|get
argument_list|(
name|src
argument_list|)
operator|.
name|toAbsolutePath
argument_list|()
operator|+
literal|" is a directory and recurse is false"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|srcIsZk
operator|==
literal|false
operator|&&
name|dstIsZk
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|SolrServerException
argument_list|(
literal|"At least one of the source and dest parameters must be prefixed with 'zk:' "
argument_list|)
throw|;
block|}
if|if
condition|(
name|dstIsZk
operator|&&
name|dst
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|dst
operator|=
literal|"/"
expr_stmt|;
comment|// for consistency, one can copy from zk: and send to zk:/
block|}
name|dst
operator|=
name|normalizeDest
argument_list|(
name|src
argument_list|,
name|dst
argument_list|)
expr_stmt|;
if|if
condition|(
name|srcIsZk
operator|&&
name|dstIsZk
condition|)
block|{
name|traverseZkTree
argument_list|(
name|zkClient
argument_list|,
name|src
argument_list|,
name|VISIT_ORDER
operator|.
name|VISIT_PRE
argument_list|,
operator|new
name|ZkCopier
argument_list|(
name|zkClient
argument_list|,
name|src
argument_list|,
name|dst
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|dstIsZk
condition|)
block|{
name|uploadToZK
argument_list|(
name|zkClient
argument_list|,
name|Paths
operator|.
name|get
argument_list|(
name|src
argument_list|)
argument_list|,
name|dst
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Copying individual files from ZK requires special handling since downloadFromZK assumes it's a directory.
comment|// This is kind of a weak test for the notion of "directory" on Zookeeper.
if|if
condition|(
name|zkClient
operator|.
name|getChildren
argument_list|(
name|src
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|downloadFromZK
argument_list|(
name|zkClient
argument_list|,
name|src
argument_list|,
name|Paths
operator|.
name|get
argument_list|(
name|dst
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|Files
operator|.
name|isDirectory
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|dst
argument_list|)
argument_list|)
condition|)
block|{
if|if
condition|(
name|dst
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
operator|==
literal|false
condition|)
name|dst
operator|+=
literal|"/"
expr_stmt|;
name|dst
operator|=
name|normalizeDest
argument_list|(
name|src
argument_list|,
name|dst
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|data
init|=
name|zkClient
operator|.
name|getData
argument_list|(
name|src
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Path
name|filename
init|=
name|Paths
operator|.
name|get
argument_list|(
name|dst
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|filename
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Writing file {}"
argument_list|,
name|filename
argument_list|)
expr_stmt|;
name|Files
operator|.
name|write
argument_list|(
name|filename
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
DECL|method|normalizeDest
specifier|private
specifier|static
name|String
name|normalizeDest
parameter_list|(
name|String
name|srcName
parameter_list|,
name|String
name|dstName
parameter_list|)
block|{
comment|// Special handling for "."
if|if
condition|(
name|dstName
operator|.
name|equals
argument_list|(
literal|"."
argument_list|)
condition|)
block|{
return|return
name|Paths
operator|.
name|get
argument_list|(
literal|"."
argument_list|)
operator|.
name|normalize
argument_list|()
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// Pull the last element of the src path and add it to the dst if the src does NOT end in a slash
comment|// If the source ends in a slash, do not append the last segment to the dest
if|if
condition|(
name|dstName
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
comment|// Dest is a directory.
name|int
name|pos
init|=
name|srcName
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
if|if
condition|(
name|pos
operator|<
literal|0
condition|)
block|{
name|dstName
operator|+=
name|srcName
expr_stmt|;
block|}
else|else
block|{
name|dstName
operator|+=
name|srcName
operator|.
name|substring
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|log
operator|.
name|info
argument_list|(
literal|"copying from '{}' to '{}'"
argument_list|,
name|srcName
argument_list|,
name|dstName
argument_list|)
expr_stmt|;
return|return
name|dstName
return|;
block|}
DECL|method|moveZnode
specifier|public
specifier|static
name|void
name|moveZnode
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|String
name|src
parameter_list|,
name|String
name|dst
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|KeeperException
throws|,
name|InterruptedException
block|{
name|String
name|destName
init|=
name|normalizeDest
argument_list|(
name|src
argument_list|,
name|dst
argument_list|)
decl_stmt|;
comment|// Special handling if the source has no children, i.e. copying just a single file.
if|if
condition|(
name|zkClient
operator|.
name|getChildren
argument_list|(
name|src
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
name|destName
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|setData
argument_list|(
name|destName
argument_list|,
name|zkClient
operator|.
name|getData
argument_list|(
name|src
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|traverseZkTree
argument_list|(
name|zkClient
argument_list|,
name|src
argument_list|,
name|VISIT_ORDER
operator|.
name|VISIT_PRE
argument_list|,
operator|new
name|ZkCopier
argument_list|(
name|zkClient
argument_list|,
name|src
argument_list|,
name|destName
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Insure all source znodes are present in dest before deleting the source.
comment|// throws error if not all there so the source is left intact. Throws error if source and dest don't match.
name|checkAllZnodesThere
argument_list|(
name|zkClient
argument_list|,
name|src
argument_list|,
name|destName
argument_list|)
expr_stmt|;
name|clean
argument_list|(
name|zkClient
argument_list|,
name|src
argument_list|)
expr_stmt|;
block|}
comment|// Insure that all the nodes in one path match the nodes in the other as a safety check before removing
comment|// the source in a 'mv' command.
DECL|method|checkAllZnodesThere
specifier|private
specifier|static
name|void
name|checkAllZnodesThere
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|String
name|src
parameter_list|,
name|String
name|dst
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
throws|,
name|SolrServerException
block|{
for|for
control|(
name|String
name|node
range|:
name|zkClient
operator|.
name|getChildren
argument_list|(
name|src
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
control|)
block|{
if|if
condition|(
name|zkClient
operator|.
name|exists
argument_list|(
name|dst
operator|+
literal|"/"
operator|+
name|node
argument_list|,
literal|true
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|SolrServerException
argument_list|(
literal|"mv command did not move node "
operator|+
name|dst
operator|+
literal|"/"
operator|+
name|node
operator|+
literal|" source left intact"
argument_list|)
throw|;
block|}
name|checkAllZnodesThere
argument_list|(
name|zkClient
argument_list|,
name|src
operator|+
literal|"/"
operator|+
name|node
argument_list|,
name|dst
operator|+
literal|"/"
operator|+
name|node
argument_list|)
expr_stmt|;
block|}
block|}
comment|// This not just a copy operation since the config manager takes care of construction the znode path to configsets
DECL|method|downConfig
specifier|public
specifier|static
name|void
name|downConfig
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|String
name|confName
parameter_list|,
name|Path
name|confPath
parameter_list|)
throws|throws
name|IOException
block|{
name|ZkConfigManager
name|manager
init|=
operator|new
name|ZkConfigManager
argument_list|(
name|zkClient
argument_list|)
decl_stmt|;
comment|// Try to download the configset
name|manager
operator|.
name|downloadConfigDir
argument_list|(
name|confName
argument_list|,
name|confPath
argument_list|)
expr_stmt|;
block|}
comment|// This not just a copy operation since the config manager takes care of construction the znode path to configsets
DECL|method|upConfig
specifier|public
specifier|static
name|void
name|upConfig
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|Path
name|confPath
parameter_list|,
name|String
name|confName
parameter_list|)
throws|throws
name|IOException
block|{
name|ZkConfigManager
name|manager
init|=
operator|new
name|ZkConfigManager
argument_list|(
name|zkClient
argument_list|)
decl_stmt|;
comment|// Try to download the configset
name|manager
operator|.
name|uploadConfigDir
argument_list|(
name|confPath
argument_list|,
name|confName
argument_list|)
expr_stmt|;
block|}
comment|// yeah, it's recursive :(
DECL|method|clean
specifier|public
specifier|static
name|void
name|clean
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|KeeperException
block|{
name|traverseZkTree
argument_list|(
name|zkClient
argument_list|,
name|path
argument_list|,
name|VISIT_ORDER
operator|.
name|VISIT_POST
argument_list|,
name|znode
lambda|->
block|{
try|try
block|{
if|if
condition|(
operator|!
name|znode
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
try|try
block|{
name|zkClient
operator|.
name|delete
argument_list|(
name|znode
argument_list|,
operator|-
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NotEmptyException
name|e
parameter_list|)
block|{
name|clean
argument_list|(
name|zkClient
argument_list|,
name|znode
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoNodeException
name|r
parameter_list|)
block|{
return|return;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|uploadToZK
specifier|public
specifier|static
name|void
name|uploadToZK
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
specifier|final
name|Path
name|fromPath
parameter_list|,
specifier|final
name|String
name|zkPath
parameter_list|,
specifier|final
name|Pattern
name|filenameExclusions
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|path
init|=
name|fromPath
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|endsWith
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
name|path
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|path
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Path
name|rootPath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|rootPath
argument_list|)
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Path "
operator|+
name|rootPath
operator|+
literal|" does not exist"
argument_list|)
throw|;
name|Files
operator|.
name|walkFileTree
argument_list|(
name|rootPath
argument_list|,
operator|new
name|SimpleFileVisitor
argument_list|<
name|Path
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FileVisitResult
name|visitFile
parameter_list|(
name|Path
name|file
parameter_list|,
name|BasicFileAttributes
name|attrs
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|filename
init|=
name|file
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|filenameExclusions
operator|!=
literal|null
operator|&&
name|filenameExclusions
operator|.
name|matcher
argument_list|(
name|filename
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"uploadToZK skipping '{}' due to filenameExclusions '{}'"
argument_list|,
name|filename
argument_list|,
name|filenameExclusions
argument_list|)
expr_stmt|;
return|return
name|FileVisitResult
operator|.
name|CONTINUE
return|;
block|}
name|String
name|zkNode
init|=
name|createZkNodeName
argument_list|(
name|zkPath
argument_list|,
name|rootPath
argument_list|,
name|file
argument_list|)
decl_stmt|;
try|try
block|{
comment|// if the path exists (and presumably we're uploading data to it) just set its data
if|if
condition|(
name|file
operator|.
name|toFile
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|ZKNODE_DATA_FILE
argument_list|)
operator|&&
name|zkClient
operator|.
name|exists
argument_list|(
name|zkNode
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|zkClient
operator|.
name|setData
argument_list|(
name|zkNode
argument_list|,
name|file
operator|.
name|toFile
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
name|zkNode
argument_list|,
name|file
operator|.
name|toFile
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
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
literal|"Error uploading file "
operator|+
name|file
operator|.
name|toString
argument_list|()
operator|+
literal|" to zookeeper path "
operator|+
name|zkNode
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
return|return
name|FileVisitResult
operator|.
name|CONTINUE
return|;
block|}
annotation|@
name|Override
specifier|public
name|FileVisitResult
name|preVisitDirectory
parameter_list|(
name|Path
name|dir
parameter_list|,
name|BasicFileAttributes
name|attrs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|dir
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"."
argument_list|)
condition|)
return|return
name|FileVisitResult
operator|.
name|SKIP_SUBTREE
return|;
return|return
name|FileVisitResult
operator|.
name|CONTINUE
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|isEphemeral
specifier|private
specifier|static
name|boolean
name|isEphemeral
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|String
name|zkPath
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|Stat
name|znodeStat
init|=
name|zkClient
operator|.
name|exists
argument_list|(
name|zkPath
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|znodeStat
operator|.
name|getEphemeralOwner
argument_list|()
operator|!=
literal|0
return|;
block|}
DECL|method|copyDataDown
specifier|private
specifier|static
name|int
name|copyDataDown
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|String
name|zkPath
parameter_list|,
name|File
name|file
parameter_list|)
throws|throws
name|IOException
throws|,
name|KeeperException
throws|,
name|InterruptedException
block|{
name|byte
index|[]
name|data
init|=
name|zkClient
operator|.
name|getData
argument_list|(
name|zkPath
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|!=
literal|null
operator|&&
name|data
operator|.
name|length
operator|>
literal|1
condition|)
block|{
comment|// There are apparently basically empty ZNodes.
name|log
operator|.
name|info
argument_list|(
literal|"Writing file {}"
argument_list|,
name|file
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Files
operator|.
name|write
argument_list|(
name|file
operator|.
name|toPath
argument_list|()
argument_list|,
name|data
argument_list|)
expr_stmt|;
return|return
name|data
operator|.
name|length
return|;
block|}
return|return
literal|0
return|;
block|}
DECL|method|downloadFromZK
specifier|public
specifier|static
name|void
name|downloadFromZK
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|String
name|zkPath
parameter_list|,
name|Path
name|file
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|List
argument_list|<
name|String
argument_list|>
name|children
init|=
name|zkClient
operator|.
name|getChildren
argument_list|(
name|zkPath
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// If it has no children, it's a leaf node, write the assoicated data from the ZNode.
comment|// Otherwise, continue recursing, but write the associated data to a special file if any
if|if
condition|(
name|children
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// If we didn't copy data down, then we also didn't create the file. But we still need a marker on the local
comment|// disk so create a dir.
if|if
condition|(
name|copyDataDown
argument_list|(
name|zkClient
argument_list|,
name|zkPath
argument_list|,
name|file
operator|.
name|toFile
argument_list|()
argument_list|)
operator|==
literal|0
condition|)
block|{
name|Files
operator|.
name|createDirectories
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|Files
operator|.
name|createDirectories
argument_list|(
name|file
argument_list|)
expr_stmt|;
comment|// Make parent dir.
comment|// ZK nodes, whether leaf or not can have data. If it's a non-leaf node and
comment|// has associated data write it into the special file.
name|copyDataDown
argument_list|(
name|zkClient
argument_list|,
name|zkPath
argument_list|,
operator|new
name|File
argument_list|(
name|file
operator|.
name|toFile
argument_list|()
argument_list|,
name|ZKNODE_DATA_FILE
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|child
range|:
name|children
control|)
block|{
name|String
name|zkChild
init|=
name|zkPath
decl_stmt|;
if|if
condition|(
name|zkChild
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
operator|==
literal|false
condition|)
name|zkChild
operator|+=
literal|"/"
expr_stmt|;
name|zkChild
operator|+=
name|child
expr_stmt|;
if|if
condition|(
name|isEphemeral
argument_list|(
name|zkClient
argument_list|,
name|zkChild
argument_list|)
condition|)
block|{
comment|// Don't copy ephemeral nodes
continue|continue;
block|}
comment|// Go deeper into the tree now
name|downloadFromZK
argument_list|(
name|zkClient
argument_list|,
name|zkChild
argument_list|,
name|file
operator|.
name|resolve
argument_list|(
name|child
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|"Error downloading files from zookeeper path "
operator|+
name|zkPath
operator|+
literal|" to "
operator|+
name|file
operator|.
name|toString
argument_list|()
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
annotation|@
name|FunctionalInterface
DECL|interface|ZkVisitor
specifier|public
interface|interface
name|ZkVisitor
block|{
comment|/**      * Visit the target path      *      * @param path the path to visit      */
DECL|method|visit
name|void
name|visit
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|KeeperException
function_decl|;
block|}
DECL|enum|VISIT_ORDER
specifier|public
enum|enum
name|VISIT_ORDER
block|{
DECL|enum constant|VISIT_PRE
name|VISIT_PRE
block|,
DECL|enum constant|VISIT_POST
name|VISIT_POST
block|}
comment|/**    * Recursively visit a zk tree rooted at path and apply the given visitor to each path. Exists as a separate method    * because some of the logic can get nuanced.    *    * @param path       the path to start from    * @param visitOrder whether to call the visitor at the at the ending or beginning of the run.    * @param visitor    the operation to perform on each path    */
DECL|method|traverseZkTree
specifier|public
specifier|static
name|void
name|traverseZkTree
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
specifier|final
name|String
name|path
parameter_list|,
specifier|final
name|VISIT_ORDER
name|visitOrder
parameter_list|,
specifier|final
name|ZkVisitor
name|visitor
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|KeeperException
block|{
if|if
condition|(
name|visitOrder
operator|==
name|VISIT_ORDER
operator|.
name|VISIT_PRE
condition|)
block|{
name|visitor
operator|.
name|visit
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|children
decl_stmt|;
try|try
block|{
name|children
operator|=
name|zkClient
operator|.
name|getChildren
argument_list|(
name|path
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoNodeException
name|r
parameter_list|)
block|{
return|return;
block|}
for|for
control|(
name|String
name|string
range|:
name|children
control|)
block|{
comment|// we can't do anything to the built-in zookeeper node
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
operator|&&
name|string
operator|.
name|equals
argument_list|(
literal|"zookeeper"
argument_list|)
condition|)
continue|continue;
if|if
condition|(
name|path
operator|.
name|startsWith
argument_list|(
literal|"/zookeeper"
argument_list|)
condition|)
continue|continue;
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|traverseZkTree
argument_list|(
name|zkClient
argument_list|,
name|path
operator|+
name|string
argument_list|,
name|visitOrder
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|traverseZkTree
argument_list|(
name|zkClient
argument_list|,
name|path
operator|+
literal|"/"
operator|+
name|string
argument_list|,
name|visitOrder
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|visitOrder
operator|==
name|VISIT_ORDER
operator|.
name|VISIT_POST
condition|)
block|{
name|visitor
operator|.
name|visit
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Take into account Windows file separaters when making a Znode's name.
DECL|method|createZkNodeName
specifier|public
specifier|static
name|String
name|createZkNodeName
parameter_list|(
name|String
name|zkRoot
parameter_list|,
name|Path
name|root
parameter_list|,
name|Path
name|file
parameter_list|)
block|{
name|String
name|relativePath
init|=
name|root
operator|.
name|relativize
argument_list|(
name|file
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// Windows shenanigans
name|String
name|separator
init|=
name|root
operator|.
name|getFileSystem
argument_list|()
operator|.
name|getSeparator
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"\\"
operator|.
name|equals
argument_list|(
name|separator
argument_list|)
condition|)
name|relativePath
operator|=
name|relativePath
operator|.
name|replaceAll
argument_list|(
literal|"\\\\"
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
comment|// It's possible that the relative path and file are the same, in which case
comment|// adding the bare slash is A Bad Idea unless it's a non-leaf data node
name|boolean
name|isNonLeafData
init|=
name|file
operator|.
name|toFile
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|ZKNODE_DATA_FILE
argument_list|)
decl_stmt|;
if|if
condition|(
name|relativePath
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|&&
name|isNonLeafData
operator|==
literal|false
condition|)
return|return
name|zkRoot
return|;
comment|// Important to have this check if the source is file:whatever/ and the destination is just zk:/
if|if
condition|(
name|zkRoot
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
operator|==
literal|false
condition|)
name|zkRoot
operator|+=
literal|"/"
expr_stmt|;
name|String
name|ret
init|=
name|zkRoot
operator|+
name|relativePath
decl_stmt|;
comment|// Special handling for data associated with non-leaf node.
if|if
condition|(
name|isNonLeafData
condition|)
block|{
comment|// special handling since what we need to do is add the data to the parent.
name|ret
operator|=
name|ret
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|ret
operator|.
name|indexOf
argument_list|(
name|ZKNODE_DATA_FILE
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|ret
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|ret
operator|=
name|ret
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|ret
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ret
return|;
block|}
block|}
end_class

begin_class
DECL|class|ZkCopier
class|class
name|ZkCopier
implements|implements
name|ZkMaintenanceUtils
operator|.
name|ZkVisitor
block|{
DECL|field|source
name|String
name|source
decl_stmt|;
DECL|field|dest
name|String
name|dest
decl_stmt|;
DECL|field|zkClient
name|SolrZkClient
name|zkClient
decl_stmt|;
DECL|method|ZkCopier
name|ZkCopier
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|String
name|source
parameter_list|,
name|String
name|dest
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|dest
operator|=
name|dest
expr_stmt|;
if|if
condition|(
name|dest
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|this
operator|.
name|dest
operator|=
name|dest
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|dest
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|zkClient
operator|=
name|zkClient
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|visit
specifier|public
name|void
name|visit
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|KeeperException
block|{
name|String
name|finalDestination
init|=
name|dest
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
name|source
argument_list|)
operator|==
literal|false
condition|)
name|finalDestination
operator|+=
literal|"/"
operator|+
name|path
operator|.
name|substring
argument_list|(
name|source
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|finalDestination
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|setData
argument_list|(
name|finalDestination
argument_list|,
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
literal|true
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


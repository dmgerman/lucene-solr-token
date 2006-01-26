begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|core
operator|.
name|SolrConfig
import|;
end_import

begin_comment
comment|//
end_comment

begin_comment
comment|// For performance reasons, we don't want to re-read
end_comment

begin_comment
comment|// config params each time an index writer is created.
end_comment

begin_comment
comment|// This config object encapsulates IndexWriter config params.
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|/**  * @author yonik  * @version $Id$  */
end_comment

begin_class
DECL|class|SolrIndexConfig
specifier|public
class|class
name|SolrIndexConfig
block|{
DECL|field|defaultsName
specifier|public
specifier|static
specifier|final
name|String
name|defaultsName
init|=
literal|"indexDefaults"
decl_stmt|;
comment|//default values
DECL|field|defUseCompoundFile
specifier|public
specifier|static
specifier|final
name|boolean
name|defUseCompoundFile
init|=
name|SolrConfig
operator|.
name|config
operator|.
name|getBool
argument_list|(
name|defaultsName
operator|+
literal|"/useCompoundFile"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
DECL|field|defMaxBufferedDocs
specifier|public
specifier|static
specifier|final
name|int
name|defMaxBufferedDocs
init|=
name|SolrConfig
operator|.
name|config
operator|.
name|getInt
argument_list|(
name|defaultsName
operator|+
literal|"/maxBufferedDocs"
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
DECL|field|defMaxMergeDocs
specifier|public
specifier|static
specifier|final
name|int
name|defMaxMergeDocs
init|=
name|SolrConfig
operator|.
name|config
operator|.
name|getInt
argument_list|(
name|defaultsName
operator|+
literal|"/maxMergeDocs"
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
DECL|field|defMergeFactor
specifier|public
specifier|static
specifier|final
name|int
name|defMergeFactor
init|=
name|SolrConfig
operator|.
name|config
operator|.
name|getInt
argument_list|(
name|defaultsName
operator|+
literal|"/mergeFactor"
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
DECL|field|defMaxFieldLength
specifier|public
specifier|static
specifier|final
name|int
name|defMaxFieldLength
init|=
name|SolrConfig
operator|.
name|config
operator|.
name|getInt
argument_list|(
name|defaultsName
operator|+
literal|"/maxFieldLength"
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
DECL|field|writeLockTimeout
specifier|public
specifier|static
specifier|final
name|int
name|writeLockTimeout
init|=
name|SolrConfig
operator|.
name|config
operator|.
name|getInt
argument_list|(
name|defaultsName
operator|+
literal|"/writeLockTimeout"
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
DECL|field|commitLockTimeout
specifier|public
specifier|static
specifier|final
name|int
name|commitLockTimeout
init|=
name|SolrConfig
operator|.
name|config
operator|.
name|getInt
argument_list|(
name|defaultsName
operator|+
literal|"/commitLockTimeout"
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|/*** These are "final" in lucene 1.9   static {     if (writeLockTimeout != -1) IndexWriter.WRITE_LOCK_TIMEOUT=writeLockTimeout;     if (commitLockTimeout != -1) IndexWriter.COMMIT_LOCK_TIMEOUT=commitLockTimeout;   }   ***/
DECL|field|useCompoundFile
specifier|public
specifier|final
name|boolean
name|useCompoundFile
decl_stmt|;
DECL|field|maxBufferedDocs
specifier|public
specifier|final
name|int
name|maxBufferedDocs
decl_stmt|;
DECL|field|maxMergeDocs
specifier|public
specifier|final
name|int
name|maxMergeDocs
decl_stmt|;
DECL|field|mergeFactor
specifier|public
specifier|final
name|int
name|mergeFactor
decl_stmt|;
DECL|field|maxFieldLength
specifier|public
specifier|final
name|int
name|maxFieldLength
decl_stmt|;
DECL|method|SolrIndexConfig
specifier|public
name|SolrIndexConfig
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|useCompoundFile
operator|=
name|SolrConfig
operator|.
name|config
operator|.
name|getBool
argument_list|(
name|prefix
operator|+
literal|"/useCompoundFile"
argument_list|,
name|defUseCompoundFile
argument_list|)
expr_stmt|;
name|maxBufferedDocs
operator|=
name|SolrConfig
operator|.
name|config
operator|.
name|getInt
argument_list|(
name|prefix
operator|+
literal|"/maxBufferedDocs"
argument_list|,
name|defMaxBufferedDocs
argument_list|)
expr_stmt|;
name|maxMergeDocs
operator|=
name|SolrConfig
operator|.
name|config
operator|.
name|getInt
argument_list|(
name|prefix
operator|+
literal|"/maxMergeDocs"
argument_list|,
name|defMaxMergeDocs
argument_list|)
expr_stmt|;
name|mergeFactor
operator|=
name|SolrConfig
operator|.
name|config
operator|.
name|getInt
argument_list|(
name|prefix
operator|+
literal|"/mergeFactor"
argument_list|,
name|defMergeFactor
argument_list|)
expr_stmt|;
name|maxFieldLength
operator|=
name|SolrConfig
operator|.
name|config
operator|.
name|getInt
argument_list|(
name|prefix
operator|+
literal|"/maxFieldLength"
argument_list|,
name|defMaxFieldLength
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|DocsEnum
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
name|index
operator|.
name|Fields
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
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|MultiFields
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
name|index
operator|.
name|Term
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
name|index
operator|.
name|Terms
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
name|index
operator|.
name|TermsEnum
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
name|search
operator|.
name|DocIdSetIterator
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
name|util
operator|.
name|BytesRef
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
name|update
operator|.
name|UpdateLog
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
name|List
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
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
operator|.
name|DistributedUpdateProcessor
operator|.
name|DistribPhase
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
operator|.
name|DistributingUpdateProcessorFactory
operator|.
name|DISTRIB_UPDATE_PARAM
import|;
end_import

begin_class
DECL|class|TestRTGBase
specifier|public
class|class
name|TestRTGBase
extends|extends
name|SolrTestCaseJ4
block|{
comment|// means we've seen the leader and have version info (i.e. we are a non-leader replica)
DECL|field|FROM_LEADER
specifier|public
specifier|static
name|String
name|FROM_LEADER
init|=
name|DistribPhase
operator|.
name|FROMLEADER
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// since we make up fake versions in these tests, we can get messed up by a DBQ with a real version
comment|// since Solr can think following updates were reordered.
annotation|@
name|Override
DECL|method|clearIndex
specifier|public
name|void
name|clearIndex
parameter_list|()
block|{
try|try
block|{
name|deleteByQueryAndGetVersion
argument_list|(
literal|"*:*"
argument_list|,
name|params
argument_list|(
literal|"_version_"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
operator|-
name|Long
operator|.
name|MAX_VALUE
argument_list|)
argument_list|,
name|DISTRIB_UPDATE_PARAM
argument_list|,
name|FROM_LEADER
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|field|model
specifier|protected
specifier|final
name|ConcurrentHashMap
argument_list|<
name|Integer
argument_list|,
name|DocInfo
argument_list|>
name|model
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|Integer
argument_list|,
name|DocInfo
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|committedModel
specifier|protected
name|Map
argument_list|<
name|Integer
argument_list|,
name|DocInfo
argument_list|>
name|committedModel
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|DocInfo
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|snapshotCount
specifier|protected
name|long
name|snapshotCount
decl_stmt|;
DECL|field|committedModelClock
specifier|protected
name|long
name|committedModelClock
decl_stmt|;
DECL|field|lastId
specifier|protected
specifier|volatile
name|int
name|lastId
decl_stmt|;
DECL|field|field
specifier|protected
specifier|final
name|String
name|field
init|=
literal|"val_l"
decl_stmt|;
DECL|field|syncArr
specifier|protected
name|Object
index|[]
name|syncArr
decl_stmt|;
DECL|field|globalLock
specifier|protected
name|Object
name|globalLock
init|=
name|this
decl_stmt|;
DECL|method|initModel
specifier|protected
name|void
name|initModel
parameter_list|(
name|int
name|ndocs
parameter_list|)
block|{
name|snapshotCount
operator|=
literal|0
expr_stmt|;
name|committedModelClock
operator|=
literal|0
expr_stmt|;
name|lastId
operator|=
literal|0
expr_stmt|;
name|syncArr
operator|=
operator|new
name|Object
index|[
name|ndocs
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ndocs
condition|;
name|i
operator|++
control|)
block|{
name|model
operator|.
name|put
argument_list|(
name|i
argument_list|,
operator|new
name|DocInfo
argument_list|(
literal|0
argument_list|,
operator|-
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|syncArr
index|[
name|i
index|]
operator|=
operator|new
name|Object
argument_list|()
expr_stmt|;
block|}
name|committedModel
operator|.
name|putAll
argument_list|(
name|model
argument_list|)
expr_stmt|;
block|}
DECL|class|DocInfo
specifier|protected
specifier|static
class|class
name|DocInfo
block|{
DECL|field|version
name|long
name|version
decl_stmt|;
DECL|field|val
name|long
name|val
decl_stmt|;
DECL|method|DocInfo
specifier|public
name|DocInfo
parameter_list|(
name|long
name|version
parameter_list|,
name|long
name|val
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|val
operator|=
name|val
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"{version="
operator|+
name|version
operator|+
literal|",val="
operator|+
name|val
operator|+
literal|"}"
return|;
block|}
block|}
DECL|method|badVersion
specifier|protected
name|long
name|badVersion
parameter_list|(
name|Random
name|rand
parameter_list|,
name|long
name|version
parameter_list|)
block|{
if|if
condition|(
name|version
operator|>
literal|0
condition|)
block|{
comment|// return a random number not equal to version
for|for
control|(
init|;
condition|;
control|)
block|{
name|long
name|badVersion
init|=
name|rand
operator|.
name|nextInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|badVersion
operator|!=
name|version
operator|&&
name|badVersion
operator|!=
literal|0
condition|)
return|return
name|badVersion
return|;
block|}
block|}
comment|// if the version does not exist, then we can only specify a positive version
for|for
control|(
init|;
condition|;
control|)
block|{
name|long
name|badVersion
init|=
name|rand
operator|.
name|nextInt
argument_list|()
operator|&
literal|0x7fffffff
decl_stmt|;
comment|// mask off sign bit
if|if
condition|(
name|badVersion
operator|!=
literal|0
condition|)
return|return
name|badVersion
return|;
block|}
block|}
DECL|method|getLatestVersions
specifier|protected
name|List
argument_list|<
name|Long
argument_list|>
name|getLatestVersions
parameter_list|()
block|{
name|List
argument_list|<
name|Long
argument_list|>
name|recentVersions
decl_stmt|;
name|UpdateLog
operator|.
name|RecentUpdates
name|startingRecentUpdates
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|getUpdateLog
argument_list|()
operator|.
name|getRecentUpdates
argument_list|()
decl_stmt|;
try|try
block|{
name|recentVersions
operator|=
name|startingRecentUpdates
operator|.
name|getVersions
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|startingRecentUpdates
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|recentVersions
return|;
block|}
DECL|method|getFirstMatch
specifier|protected
name|int
name|getFirstMatch
parameter_list|(
name|IndexReader
name|r
parameter_list|,
name|Term
name|t
parameter_list|)
throws|throws
name|IOException
block|{
name|Fields
name|fields
init|=
name|MultiFields
operator|.
name|getFields
argument_list|(
name|r
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
return|return
operator|-
literal|1
return|;
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|t
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
return|return
operator|-
literal|1
return|;
name|BytesRef
name|termBytes
init|=
name|t
operator|.
name|bytes
argument_list|()
decl_stmt|;
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|termBytes
argument_list|,
literal|false
argument_list|)
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|DocsEnum
name|docs
init|=
name|termsEnum
operator|.
name|docs
argument_list|(
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|r
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|int
name|id
init|=
name|docs
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|int
name|next
init|=
name|docs
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|,
name|next
argument_list|)
expr_stmt|;
block|}
return|return
name|id
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|?
operator|-
literal|1
else|:
name|id
return|;
block|}
block|}
end_class

end_unit


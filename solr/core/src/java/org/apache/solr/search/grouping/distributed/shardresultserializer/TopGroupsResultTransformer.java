begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search.grouping.distributed.shardresultserializer
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|grouping
operator|.
name|distributed
operator|.
name|shardresultserializer
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
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
name|document
operator|.
name|DocumentStoredFieldVisitor
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
name|FieldDoc
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
name|ScoreDoc
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
name|Sort
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
name|TopDocs
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
name|grouping
operator|.
name|GroupDocs
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
name|grouping
operator|.
name|TopGroups
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
name|lucene
operator|.
name|util
operator|.
name|CharsRef
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
name|UnicodeUtil
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
name|NamedList
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
name|handler
operator|.
name|component
operator|.
name|ResponseBuilder
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
name|handler
operator|.
name|component
operator|.
name|ShardDoc
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
name|schema
operator|.
name|FieldType
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
name|schema
operator|.
name|SchemaField
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
name|search
operator|.
name|grouping
operator|.
name|Command
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
name|search
operator|.
name|grouping
operator|.
name|distributed
operator|.
name|command
operator|.
name|QueryCommand
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
name|search
operator|.
name|grouping
operator|.
name|distributed
operator|.
name|command
operator|.
name|QueryCommandResult
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
name|search
operator|.
name|grouping
operator|.
name|distributed
operator|.
name|command
operator|.
name|TopGroupsFieldCommand
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
name|ArrayList
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

begin_comment
comment|/**  * Implementation for transforming {@link TopGroups} and {@link TopDocs} into a {@link NamedList} structure and  * visa versa.  */
end_comment

begin_class
DECL|class|TopGroupsResultTransformer
specifier|public
class|class
name|TopGroupsResultTransformer
implements|implements
name|ShardResultTransformer
argument_list|<
name|List
argument_list|<
name|Command
argument_list|>
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
argument_list|>
block|{
DECL|field|rb
specifier|private
specifier|final
name|ResponseBuilder
name|rb
decl_stmt|;
DECL|method|TopGroupsResultTransformer
specifier|public
name|TopGroupsResultTransformer
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
block|{
name|this
operator|.
name|rb
operator|=
name|rb
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|transform
specifier|public
name|NamedList
name|transform
parameter_list|(
name|List
argument_list|<
name|Command
argument_list|>
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|NamedList
argument_list|<
name|NamedList
argument_list|>
name|result
init|=
operator|new
name|NamedList
argument_list|<
name|NamedList
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Command
name|command
range|:
name|data
control|)
block|{
name|NamedList
name|commandResult
decl_stmt|;
if|if
condition|(
name|TopGroupsFieldCommand
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|command
argument_list|)
condition|)
block|{
name|TopGroupsFieldCommand
name|fieldCommand
init|=
operator|(
name|TopGroupsFieldCommand
operator|)
name|command
decl_stmt|;
name|SchemaField
name|groupField
init|=
name|rb
operator|.
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|fieldCommand
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|commandResult
operator|=
name|serializeTopGroups
argument_list|(
name|fieldCommand
operator|.
name|result
argument_list|()
argument_list|,
name|groupField
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|QueryCommand
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|command
argument_list|)
condition|)
block|{
name|QueryCommand
name|queryCommand
init|=
operator|(
name|QueryCommand
operator|)
name|command
decl_stmt|;
name|commandResult
operator|=
name|serializeTopDocs
argument_list|(
name|queryCommand
operator|.
name|result
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|commandResult
operator|=
literal|null
expr_stmt|;
block|}
name|result
operator|.
name|add
argument_list|(
name|command
operator|.
name|getKey
argument_list|()
argument_list|,
name|commandResult
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * {@inheritDoc}    */
DECL|method|transformToNative
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|transformToNative
parameter_list|(
name|NamedList
argument_list|<
name|NamedList
argument_list|>
name|shardResponse
parameter_list|,
name|Sort
name|groupSort
parameter_list|,
name|Sort
name|sortWithinGroup
parameter_list|,
name|String
name|shard
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|NamedList
argument_list|>
name|entry
range|:
name|shardResponse
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|NamedList
name|commandResult
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Integer
name|totalGroupedHitCount
init|=
operator|(
name|Integer
operator|)
name|commandResult
operator|.
name|get
argument_list|(
literal|"totalGroupedHitCount"
argument_list|)
decl_stmt|;
name|Integer
name|totalHits
init|=
operator|(
name|Integer
operator|)
name|commandResult
operator|.
name|get
argument_list|(
literal|"totalHits"
argument_list|)
decl_stmt|;
if|if
condition|(
name|totalHits
operator|!=
literal|null
condition|)
block|{
name|Integer
name|matches
init|=
operator|(
name|Integer
operator|)
name|commandResult
operator|.
name|get
argument_list|(
literal|"matches"
argument_list|)
decl_stmt|;
name|Float
name|maxScore
init|=
operator|(
name|Float
operator|)
name|commandResult
operator|.
name|get
argument_list|(
literal|"maxScore"
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxScore
operator|==
literal|null
condition|)
block|{
name|maxScore
operator|=
name|Float
operator|.
name|NaN
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|documents
init|=
operator|(
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
operator|)
name|commandResult
operator|.
name|get
argument_list|(
literal|"documents"
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|scoreDocs
init|=
operator|new
name|ScoreDoc
index|[
name|documents
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|document
range|:
name|documents
control|)
block|{
name|Object
name|uniqueId
init|=
name|document
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Float
name|score
init|=
operator|(
name|Float
operator|)
name|document
operator|.
name|get
argument_list|(
literal|"score"
argument_list|)
decl_stmt|;
if|if
condition|(
name|score
operator|==
literal|null
condition|)
block|{
name|score
operator|=
name|Float
operator|.
name|NaN
expr_stmt|;
block|}
name|Object
index|[]
name|sortValues
init|=
operator|(
operator|(
name|List
operator|)
name|document
operator|.
name|get
argument_list|(
literal|"sortValues"
argument_list|)
operator|)
operator|.
name|toArray
argument_list|()
decl_stmt|;
name|scoreDocs
index|[
name|j
operator|++
index|]
operator|=
operator|new
name|ShardDoc
argument_list|(
name|score
argument_list|,
name|sortValues
argument_list|,
name|uniqueId
argument_list|,
name|shard
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|put
argument_list|(
name|key
argument_list|,
operator|new
name|QueryCommandResult
argument_list|(
operator|new
name|TopDocs
argument_list|(
name|totalHits
argument_list|,
name|scoreDocs
argument_list|,
name|maxScore
argument_list|)
argument_list|,
name|matches
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|Integer
name|totalHitCount
init|=
operator|(
name|Integer
operator|)
name|commandResult
operator|.
name|get
argument_list|(
literal|"totalHitCount"
argument_list|)
decl_stmt|;
name|Integer
name|totalGroupCount
init|=
operator|(
name|Integer
operator|)
name|commandResult
operator|.
name|get
argument_list|(
literal|"totalGroupCount"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|GroupDocs
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|groupDocs
init|=
operator|new
name|ArrayList
argument_list|<
name|GroupDocs
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|totalGroupCount
operator|==
literal|null
condition|?
literal|2
else|:
literal|3
init|;
name|i
operator|<
name|commandResult
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|groupValue
init|=
name|commandResult
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|NamedList
argument_list|<
name|Object
argument_list|>
name|groupResult
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|commandResult
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Integer
name|totalGroupHits
init|=
operator|(
name|Integer
operator|)
name|groupResult
operator|.
name|get
argument_list|(
literal|"totalHits"
argument_list|)
decl_stmt|;
name|Float
name|maxScore
init|=
operator|(
name|Float
operator|)
name|groupResult
operator|.
name|get
argument_list|(
literal|"maxScore"
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxScore
operator|==
literal|null
condition|)
block|{
name|maxScore
operator|=
name|Float
operator|.
name|NaN
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|documents
init|=
operator|(
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
operator|)
name|groupResult
operator|.
name|get
argument_list|(
literal|"documents"
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|scoreDocs
init|=
operator|new
name|ScoreDoc
index|[
name|documents
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|document
range|:
name|documents
control|)
block|{
name|Object
name|uniqueId
init|=
name|document
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Float
name|score
init|=
operator|(
name|Float
operator|)
name|document
operator|.
name|get
argument_list|(
literal|"score"
argument_list|)
decl_stmt|;
if|if
condition|(
name|score
operator|==
literal|null
condition|)
block|{
name|score
operator|=
name|Float
operator|.
name|NaN
expr_stmt|;
block|}
name|Object
index|[]
name|sortValues
init|=
operator|(
operator|(
name|List
operator|)
name|document
operator|.
name|get
argument_list|(
literal|"sortValues"
argument_list|)
operator|)
operator|.
name|toArray
argument_list|()
decl_stmt|;
name|scoreDocs
index|[
name|j
operator|++
index|]
operator|=
operator|new
name|ShardDoc
argument_list|(
name|score
argument_list|,
name|sortValues
argument_list|,
name|uniqueId
argument_list|,
name|shard
argument_list|)
expr_stmt|;
block|}
name|BytesRef
name|groupValueRef
init|=
name|groupValue
operator|!=
literal|null
condition|?
operator|new
name|BytesRef
argument_list|(
name|groupValue
argument_list|)
else|:
literal|null
decl_stmt|;
name|groupDocs
operator|.
name|add
argument_list|(
operator|new
name|GroupDocs
argument_list|<
name|BytesRef
argument_list|>
argument_list|(
name|maxScore
argument_list|,
name|totalGroupHits
argument_list|,
name|scoreDocs
argument_list|,
name|groupValueRef
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|GroupDocs
argument_list|<
name|BytesRef
argument_list|>
index|[]
name|groupDocsArr
init|=
name|groupDocs
operator|.
name|toArray
argument_list|(
operator|new
name|GroupDocs
index|[
name|groupDocs
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|TopGroups
argument_list|<
name|BytesRef
argument_list|>
name|topGroups
init|=
operator|new
name|TopGroups
argument_list|<
name|BytesRef
argument_list|>
argument_list|(
name|groupSort
operator|.
name|getSort
argument_list|()
argument_list|,
name|sortWithinGroup
operator|.
name|getSort
argument_list|()
argument_list|,
name|totalHitCount
argument_list|,
name|totalGroupedHitCount
argument_list|,
name|groupDocsArr
argument_list|)
decl_stmt|;
if|if
condition|(
name|totalGroupCount
operator|!=
literal|null
condition|)
block|{
name|topGroups
operator|=
operator|new
name|TopGroups
argument_list|<
name|BytesRef
argument_list|>
argument_list|(
name|topGroups
argument_list|,
name|totalGroupCount
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|topGroups
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|serializeTopGroups
specifier|protected
name|NamedList
name|serializeTopGroups
parameter_list|(
name|TopGroups
argument_list|<
name|BytesRef
argument_list|>
name|data
parameter_list|,
name|SchemaField
name|groupField
parameter_list|)
throws|throws
name|IOException
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|result
init|=
operator|new
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
literal|"totalGroupedHitCount"
argument_list|,
name|data
operator|.
name|totalGroupedHitCount
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
literal|"totalHitCount"
argument_list|,
name|data
operator|.
name|totalHitCount
argument_list|)
expr_stmt|;
if|if
condition|(
name|data
operator|.
name|totalGroupCount
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
literal|"totalGroupCount"
argument_list|,
name|data
operator|.
name|totalGroupCount
argument_list|)
expr_stmt|;
block|}
name|CharsRef
name|spare
init|=
operator|new
name|CharsRef
argument_list|()
decl_stmt|;
name|SchemaField
name|uniqueField
init|=
name|rb
operator|.
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
for|for
control|(
name|GroupDocs
argument_list|<
name|BytesRef
argument_list|>
name|searchGroup
range|:
name|data
operator|.
name|groups
control|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|groupResult
init|=
operator|new
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|groupResult
operator|.
name|add
argument_list|(
literal|"totalHits"
argument_list|,
name|searchGroup
operator|.
name|totalHits
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Float
operator|.
name|isNaN
argument_list|(
name|searchGroup
operator|.
name|maxScore
argument_list|)
condition|)
block|{
name|groupResult
operator|.
name|add
argument_list|(
literal|"maxScore"
argument_list|,
name|searchGroup
operator|.
name|maxScore
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
name|documents
init|=
operator|new
name|ArrayList
argument_list|<
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|searchGroup
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|document
init|=
operator|new
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|documents
operator|.
name|add
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|retrieveDocument
argument_list|(
name|uniqueField
argument_list|,
name|searchGroup
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
literal|"id"
argument_list|,
name|uniqueField
operator|.
name|getType
argument_list|()
operator|.
name|toExternal
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|uniqueField
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Float
operator|.
name|isNaN
argument_list|(
name|searchGroup
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|score
argument_list|)
condition|)
block|{
name|document
operator|.
name|add
argument_list|(
literal|"score"
argument_list|,
name|searchGroup
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|score
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
operator|(
name|searchGroup
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|instanceof
name|FieldDoc
operator|)
condition|)
block|{
continue|continue;
block|}
name|FieldDoc
name|fieldDoc
init|=
operator|(
name|FieldDoc
operator|)
name|searchGroup
operator|.
name|scoreDocs
index|[
name|i
index|]
decl_stmt|;
name|Object
index|[]
name|convertedSortValues
init|=
operator|new
name|Object
index|[
name|fieldDoc
operator|.
name|fields
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|fieldDoc
operator|.
name|fields
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|Object
name|sortValue
init|=
name|fieldDoc
operator|.
name|fields
index|[
name|j
index|]
decl_stmt|;
name|Sort
name|sortWithinGroup
init|=
name|rb
operator|.
name|getGroupingSpec
argument_list|()
operator|.
name|getSortWithinGroup
argument_list|()
decl_stmt|;
name|SchemaField
name|field
init|=
name|sortWithinGroup
operator|.
name|getSort
argument_list|()
index|[
name|j
index|]
operator|.
name|getField
argument_list|()
operator|!=
literal|null
condition|?
name|rb
operator|.
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getFieldOrNull
argument_list|(
name|sortWithinGroup
operator|.
name|getSort
argument_list|()
index|[
name|j
index|]
operator|.
name|getField
argument_list|()
argument_list|)
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
name|FieldType
name|fieldType
init|=
name|field
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|sortValue
operator|instanceof
name|BytesRef
condition|)
block|{
name|UnicodeUtil
operator|.
name|UTF8toUTF16
argument_list|(
operator|(
name|BytesRef
operator|)
name|sortValue
argument_list|,
name|spare
argument_list|)
expr_stmt|;
name|String
name|indexedValue
init|=
name|spare
operator|.
name|toString
argument_list|()
decl_stmt|;
name|sortValue
operator|=
name|fieldType
operator|.
name|toObject
argument_list|(
name|field
operator|.
name|createField
argument_list|(
name|fieldType
operator|.
name|indexedToReadable
argument_list|(
name|indexedValue
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sortValue
operator|instanceof
name|String
condition|)
block|{
name|sortValue
operator|=
name|fieldType
operator|.
name|toObject
argument_list|(
name|field
operator|.
name|createField
argument_list|(
name|fieldType
operator|.
name|indexedToReadable
argument_list|(
operator|(
name|String
operator|)
name|sortValue
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|convertedSortValues
index|[
name|j
index|]
operator|=
name|sortValue
expr_stmt|;
block|}
name|document
operator|.
name|add
argument_list|(
literal|"sortValues"
argument_list|,
name|convertedSortValues
argument_list|)
expr_stmt|;
block|}
name|groupResult
operator|.
name|add
argument_list|(
literal|"documents"
argument_list|,
name|documents
argument_list|)
expr_stmt|;
name|String
name|groupValue
init|=
name|searchGroup
operator|.
name|groupValue
operator|!=
literal|null
condition|?
name|groupField
operator|.
name|getType
argument_list|()
operator|.
name|indexedToReadable
argument_list|(
name|searchGroup
operator|.
name|groupValue
operator|.
name|utf8ToString
argument_list|()
argument_list|)
else|:
literal|null
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|groupValue
argument_list|,
name|groupResult
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|serializeTopDocs
specifier|protected
name|NamedList
name|serializeTopDocs
parameter_list|(
name|QueryCommandResult
name|result
parameter_list|)
throws|throws
name|IOException
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|queryResult
init|=
operator|new
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|queryResult
operator|.
name|add
argument_list|(
literal|"matches"
argument_list|,
name|result
operator|.
name|getMatches
argument_list|()
argument_list|)
expr_stmt|;
name|queryResult
operator|.
name|add
argument_list|(
literal|"totalHits"
argument_list|,
name|result
operator|.
name|getTopDocs
argument_list|()
operator|.
name|totalHits
argument_list|)
expr_stmt|;
if|if
condition|(
name|rb
operator|.
name|getGroupingSpec
argument_list|()
operator|.
name|isNeedScore
argument_list|()
condition|)
block|{
name|queryResult
operator|.
name|add
argument_list|(
literal|"maxScore"
argument_list|,
name|result
operator|.
name|getTopDocs
argument_list|()
operator|.
name|getMaxScore
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|NamedList
argument_list|>
name|documents
init|=
operator|new
name|ArrayList
argument_list|<
name|NamedList
argument_list|>
argument_list|()
decl_stmt|;
name|queryResult
operator|.
name|add
argument_list|(
literal|"documents"
argument_list|,
name|documents
argument_list|)
expr_stmt|;
name|SchemaField
name|uniqueField
init|=
name|rb
operator|.
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getUniqueKeyField
argument_list|()
decl_stmt|;
name|CharsRef
name|spare
init|=
operator|new
name|CharsRef
argument_list|()
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|scoreDoc
range|:
name|result
operator|.
name|getTopDocs
argument_list|()
operator|.
name|scoreDocs
control|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|document
init|=
operator|new
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|documents
operator|.
name|add
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|retrieveDocument
argument_list|(
name|uniqueField
argument_list|,
name|scoreDoc
operator|.
name|doc
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
literal|"id"
argument_list|,
name|uniqueField
operator|.
name|getType
argument_list|()
operator|.
name|toExternal
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|uniqueField
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|rb
operator|.
name|getGroupingSpec
argument_list|()
operator|.
name|isNeedScore
argument_list|()
condition|)
block|{
name|document
operator|.
name|add
argument_list|(
literal|"score"
argument_list|,
name|scoreDoc
operator|.
name|score
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|FieldDoc
operator|.
name|class
operator|.
name|isInstance
argument_list|(
name|scoreDoc
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|FieldDoc
name|fieldDoc
init|=
operator|(
name|FieldDoc
operator|)
name|scoreDoc
decl_stmt|;
name|Object
index|[]
name|convertedSortValues
init|=
operator|new
name|Object
index|[
name|fieldDoc
operator|.
name|fields
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|fieldDoc
operator|.
name|fields
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|Object
name|sortValue
init|=
name|fieldDoc
operator|.
name|fields
index|[
name|j
index|]
decl_stmt|;
name|Sort
name|groupSort
init|=
name|rb
operator|.
name|getGroupingSpec
argument_list|()
operator|.
name|getGroupSort
argument_list|()
decl_stmt|;
name|SchemaField
name|field
init|=
name|groupSort
operator|.
name|getSort
argument_list|()
index|[
name|j
index|]
operator|.
name|getField
argument_list|()
operator|!=
literal|null
condition|?
name|rb
operator|.
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getFieldOrNull
argument_list|(
name|groupSort
operator|.
name|getSort
argument_list|()
index|[
name|j
index|]
operator|.
name|getField
argument_list|()
argument_list|)
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
name|FieldType
name|fieldType
init|=
name|field
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|sortValue
operator|instanceof
name|BytesRef
condition|)
block|{
name|UnicodeUtil
operator|.
name|UTF8toUTF16
argument_list|(
operator|(
name|BytesRef
operator|)
name|sortValue
argument_list|,
name|spare
argument_list|)
expr_stmt|;
name|String
name|indexedValue
init|=
name|spare
operator|.
name|toString
argument_list|()
decl_stmt|;
name|sortValue
operator|=
name|fieldType
operator|.
name|toObject
argument_list|(
name|field
operator|.
name|createField
argument_list|(
name|fieldType
operator|.
name|indexedToReadable
argument_list|(
name|indexedValue
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sortValue
operator|instanceof
name|String
condition|)
block|{
name|sortValue
operator|=
name|fieldType
operator|.
name|toObject
argument_list|(
name|field
operator|.
name|createField
argument_list|(
name|fieldType
operator|.
name|indexedToReadable
argument_list|(
operator|(
name|String
operator|)
name|sortValue
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|convertedSortValues
index|[
name|j
index|]
operator|=
name|sortValue
expr_stmt|;
block|}
name|document
operator|.
name|add
argument_list|(
literal|"sortValues"
argument_list|,
name|convertedSortValues
argument_list|)
expr_stmt|;
block|}
return|return
name|queryResult
return|;
block|}
DECL|method|retrieveDocument
specifier|private
name|Document
name|retrieveDocument
parameter_list|(
specifier|final
name|SchemaField
name|uniqueField
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|DocumentStoredFieldVisitor
name|visitor
init|=
operator|new
name|DocumentStoredFieldVisitor
argument_list|(
name|uniqueField
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|rb
operator|.
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|doc
argument_list|(
name|doc
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
return|return
name|visitor
operator|.
name|getDocument
argument_list|()
return|;
block|}
block|}
end_class

end_unit


begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.grouping
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
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|ExitableDirectoryReader
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|BooleanClause
operator|.
name|Occur
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
name|BooleanQuery
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
name|Collector
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
name|MultiCollector
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
name|Query
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
name|TimeLimitingCollector
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
name|TotalHitCountCollector
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
name|AbstractAllGroupHeadsCollector
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
name|function
operator|.
name|FunctionAllGroupHeadsCollector
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
name|term
operator|.
name|TermAllGroupHeadsCollector
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
name|BitDocSet
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
name|DocSet
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
name|DocSetCollector
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
name|QueryResult
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
name|QueryUtils
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
name|SolrIndexSearcher
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
name|SolrIndexSearcher
operator|.
name|ProcessedFilter
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
name|shardresultserializer
operator|.
name|ShardResultTransformer
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
comment|/**  * Responsible for executing a search with a number of {@link Command} instances.  * A typical search can have more then one {@link Command} instances.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|CommandHandler
specifier|public
class|class
name|CommandHandler
block|{
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|queryCommand
specifier|private
name|QueryCommand
name|queryCommand
decl_stmt|;
DECL|field|commands
specifier|private
name|List
argument_list|<
name|Command
argument_list|>
name|commands
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|searcher
specifier|private
name|SolrIndexSearcher
name|searcher
decl_stmt|;
DECL|field|needDocSet
specifier|private
name|boolean
name|needDocSet
init|=
literal|false
decl_stmt|;
DECL|field|truncateGroups
specifier|private
name|boolean
name|truncateGroups
init|=
literal|false
decl_stmt|;
DECL|field|includeHitCount
specifier|private
name|boolean
name|includeHitCount
init|=
literal|false
decl_stmt|;
DECL|method|setQueryCommand
specifier|public
name|Builder
name|setQueryCommand
parameter_list|(
name|QueryCommand
name|queryCommand
parameter_list|)
block|{
name|this
operator|.
name|queryCommand
operator|=
name|queryCommand
expr_stmt|;
name|this
operator|.
name|needDocSet
operator|=
operator|(
name|queryCommand
operator|.
name|getFlags
argument_list|()
operator|&
name|SolrIndexSearcher
operator|.
name|GET_DOCSET
operator|)
operator|!=
literal|0
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addCommandField
specifier|public
name|Builder
name|addCommandField
parameter_list|(
name|Command
name|commandField
parameter_list|)
block|{
name|commands
operator|.
name|add
argument_list|(
name|commandField
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setSearcher
specifier|public
name|Builder
name|setSearcher
parameter_list|(
name|SolrIndexSearcher
name|searcher
parameter_list|)
block|{
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets whether to compute a {@link DocSet}.      * May override the value set by {@link #setQueryCommand(org.apache.solr.search.QueryCommand)}.      *      * @param needDocSet Whether to compute a {@link DocSet}      * @return this      */
DECL|method|setNeedDocSet
specifier|public
name|Builder
name|setNeedDocSet
parameter_list|(
name|boolean
name|needDocSet
parameter_list|)
block|{
name|this
operator|.
name|needDocSet
operator|=
name|needDocSet
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setTruncateGroups
specifier|public
name|Builder
name|setTruncateGroups
parameter_list|(
name|boolean
name|truncateGroups
parameter_list|)
block|{
name|this
operator|.
name|truncateGroups
operator|=
name|truncateGroups
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setIncludeHitCount
specifier|public
name|Builder
name|setIncludeHitCount
parameter_list|(
name|boolean
name|includeHitCount
parameter_list|)
block|{
name|this
operator|.
name|includeHitCount
operator|=
name|includeHitCount
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build
specifier|public
name|CommandHandler
name|build
parameter_list|()
block|{
if|if
condition|(
name|queryCommand
operator|==
literal|null
operator|||
name|searcher
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"All fields must be set"
argument_list|)
throw|;
block|}
return|return
operator|new
name|CommandHandler
argument_list|(
name|queryCommand
argument_list|,
name|commands
argument_list|,
name|searcher
argument_list|,
name|needDocSet
argument_list|,
name|truncateGroups
argument_list|,
name|includeHitCount
argument_list|)
return|;
block|}
block|}
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
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
DECL|field|queryCommand
specifier|private
specifier|final
name|QueryCommand
name|queryCommand
decl_stmt|;
DECL|field|commands
specifier|private
specifier|final
name|List
argument_list|<
name|Command
argument_list|>
name|commands
decl_stmt|;
DECL|field|searcher
specifier|private
specifier|final
name|SolrIndexSearcher
name|searcher
decl_stmt|;
DECL|field|needDocset
specifier|private
specifier|final
name|boolean
name|needDocset
decl_stmt|;
DECL|field|truncateGroups
specifier|private
specifier|final
name|boolean
name|truncateGroups
decl_stmt|;
DECL|field|includeHitCount
specifier|private
specifier|final
name|boolean
name|includeHitCount
decl_stmt|;
DECL|field|partialResults
specifier|private
name|boolean
name|partialResults
init|=
literal|false
decl_stmt|;
DECL|field|totalHitCount
specifier|private
name|int
name|totalHitCount
decl_stmt|;
DECL|field|docSet
specifier|private
name|DocSet
name|docSet
decl_stmt|;
DECL|method|CommandHandler
specifier|private
name|CommandHandler
parameter_list|(
name|QueryCommand
name|queryCommand
parameter_list|,
name|List
argument_list|<
name|Command
argument_list|>
name|commands
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needDocset
parameter_list|,
name|boolean
name|truncateGroups
parameter_list|,
name|boolean
name|includeHitCount
parameter_list|)
block|{
name|this
operator|.
name|queryCommand
operator|=
name|queryCommand
expr_stmt|;
name|this
operator|.
name|commands
operator|=
name|commands
expr_stmt|;
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
name|this
operator|.
name|needDocset
operator|=
name|needDocset
expr_stmt|;
name|this
operator|.
name|truncateGroups
operator|=
name|truncateGroups
expr_stmt|;
name|this
operator|.
name|includeHitCount
operator|=
name|includeHitCount
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|nrOfCommands
init|=
name|commands
operator|.
name|size
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Collector
argument_list|>
name|collectors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|nrOfCommands
argument_list|)
decl_stmt|;
for|for
control|(
name|Command
name|command
range|:
name|commands
control|)
block|{
name|collectors
operator|.
name|addAll
argument_list|(
name|command
operator|.
name|create
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ProcessedFilter
name|filter
init|=
name|searcher
operator|.
name|getProcessedFilter
argument_list|(
name|queryCommand
operator|.
name|getFilter
argument_list|()
argument_list|,
name|queryCommand
operator|.
name|getFilterList
argument_list|()
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|QueryUtils
operator|.
name|makeQueryable
argument_list|(
name|queryCommand
operator|.
name|getQuery
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|truncateGroups
condition|)
block|{
name|docSet
operator|=
name|computeGroupedDocSet
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|collectors
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|needDocset
condition|)
block|{
name|docSet
operator|=
name|computeDocSet
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|collectors
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|collectors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|searchWithTimeLimiter
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|MultiCollector
operator|.
name|wrap
argument_list|(
name|collectors
operator|.
name|toArray
argument_list|(
operator|new
name|Collector
index|[
name|nrOfCommands
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|searchWithTimeLimiter
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|computeGroupedDocSet
specifier|private
name|DocSet
name|computeGroupedDocSet
parameter_list|(
name|Query
name|query
parameter_list|,
name|ProcessedFilter
name|filter
parameter_list|,
name|List
argument_list|<
name|Collector
argument_list|>
name|collectors
parameter_list|)
throws|throws
name|IOException
block|{
name|Command
name|firstCommand
init|=
name|commands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|field
init|=
name|firstCommand
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|SchemaField
name|sf
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|FieldType
name|fieldType
init|=
name|sf
operator|.
name|getType
argument_list|()
decl_stmt|;
specifier|final
name|AbstractAllGroupHeadsCollector
name|allGroupHeadsCollector
decl_stmt|;
if|if
condition|(
name|fieldType
operator|.
name|getNumericType
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|ValueSource
name|vs
init|=
name|fieldType
operator|.
name|getValueSource
argument_list|(
name|sf
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|allGroupHeadsCollector
operator|=
operator|new
name|FunctionAllGroupHeadsCollector
argument_list|(
name|vs
argument_list|,
operator|new
name|HashMap
argument_list|()
argument_list|,
name|firstCommand
operator|.
name|getSortWithinGroup
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|allGroupHeadsCollector
operator|=
name|TermAllGroupHeadsCollector
operator|.
name|create
argument_list|(
name|firstCommand
operator|.
name|getKey
argument_list|()
argument_list|,
name|firstCommand
operator|.
name|getSortWithinGroup
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|collectors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|searchWithTimeLimiter
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|allGroupHeadsCollector
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|collectors
operator|.
name|add
argument_list|(
name|allGroupHeadsCollector
argument_list|)
expr_stmt|;
name|searchWithTimeLimiter
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|MultiCollector
operator|.
name|wrap
argument_list|(
name|collectors
operator|.
name|toArray
argument_list|(
operator|new
name|Collector
index|[
name|collectors
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BitDocSet
argument_list|(
name|allGroupHeadsCollector
operator|.
name|retrieveGroupHeads
argument_list|(
name|searcher
operator|.
name|maxDoc
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|computeDocSet
specifier|private
name|DocSet
name|computeDocSet
parameter_list|(
name|Query
name|query
parameter_list|,
name|ProcessedFilter
name|filter
parameter_list|,
name|List
argument_list|<
name|Collector
argument_list|>
name|collectors
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|maxDoc
init|=
name|searcher
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
specifier|final
name|DocSetCollector
name|docSetCollector
init|=
operator|new
name|DocSetCollector
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Collector
argument_list|>
name|allCollectors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|collectors
argument_list|)
decl_stmt|;
name|allCollectors
operator|.
name|add
argument_list|(
name|docSetCollector
argument_list|)
expr_stmt|;
name|searchWithTimeLimiter
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|MultiCollector
operator|.
name|wrap
argument_list|(
name|allCollectors
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|docSetCollector
operator|.
name|getDocSet
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|processResult
specifier|public
name|NamedList
name|processResult
parameter_list|(
name|QueryResult
name|queryResult
parameter_list|,
name|ShardResultTransformer
name|transformer
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|docSet
operator|!=
literal|null
condition|)
block|{
name|queryResult
operator|.
name|setDocSet
argument_list|(
name|docSet
argument_list|)
expr_stmt|;
block|}
name|queryResult
operator|.
name|setPartialResults
argument_list|(
name|partialResults
argument_list|)
expr_stmt|;
return|return
name|transformer
operator|.
name|transform
argument_list|(
name|commands
argument_list|)
return|;
block|}
comment|/**    * Invokes search with the specified filter and collector.      * If a time limit has been specified then wrap the collector in the TimeLimitingCollector    */
DECL|method|searchWithTimeLimiter
specifier|private
name|void
name|searchWithTimeLimiter
parameter_list|(
name|Query
name|query
parameter_list|,
name|ProcessedFilter
name|filter
parameter_list|,
name|Collector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|queryCommand
operator|.
name|getTimeAllowed
argument_list|()
operator|>
literal|0
condition|)
block|{
name|collector
operator|=
operator|new
name|TimeLimitingCollector
argument_list|(
name|collector
argument_list|,
name|TimeLimitingCollector
operator|.
name|getGlobalCounter
argument_list|()
argument_list|,
name|queryCommand
operator|.
name|getTimeAllowed
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|TotalHitCountCollector
name|hitCountCollector
init|=
operator|new
name|TotalHitCountCollector
argument_list|()
decl_stmt|;
if|if
condition|(
name|includeHitCount
condition|)
block|{
name|collector
operator|=
name|MultiCollector
operator|.
name|wrap
argument_list|(
name|collector
argument_list|,
name|hitCountCollector
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|filter
operator|.
name|filter
operator|!=
literal|null
condition|)
block|{
name|query
operator|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
name|query
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
operator|.
name|add
argument_list|(
name|filter
operator|.
name|filter
argument_list|,
name|Occur
operator|.
name|FILTER
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|filter
operator|.
name|postFilter
operator|!=
literal|null
condition|)
block|{
name|filter
operator|.
name|postFilter
operator|.
name|setLastDelegate
argument_list|(
name|collector
argument_list|)
expr_stmt|;
name|collector
operator|=
name|filter
operator|.
name|postFilter
expr_stmt|;
block|}
try|try
block|{
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|collector
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeLimitingCollector
operator|.
name|TimeExceededException
decl||
name|ExitableDirectoryReader
operator|.
name|ExitingReaderException
name|x
parameter_list|)
block|{
name|partialResults
operator|=
literal|true
expr_stmt|;
name|logger
operator|.
name|warn
argument_list|(
literal|"Query: "
operator|+
name|query
operator|+
literal|"; "
operator|+
name|x
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includeHitCount
condition|)
block|{
name|totalHitCount
operator|=
name|hitCountCollector
operator|.
name|getTotalHits
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getTotalHitCount
specifier|public
name|int
name|getTotalHitCount
parameter_list|()
block|{
return|return
name|totalHitCount
return|;
block|}
block|}
end_class

end_unit


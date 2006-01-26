begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
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
name|search
operator|.
name|*
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
name|Document
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Level
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
name|net
operator|.
name|URL
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
name|util
operator|.
name|StrUtils
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
name|search
operator|.
name|*
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
name|IndexSchema
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
name|SolrCore
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
name|SolrInfoMBean
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
name|SolrException
import|;
end_import

begin_comment
comment|/**  * @author yonik  * @version $Id: StandardRequestHandler.java,v 1.17 2005/12/02 04:31:06 yonik Exp $  */
end_comment

begin_class
DECL|class|StandardRequestHandler
specifier|public
class|class
name|StandardRequestHandler
implements|implements
name|SolrRequestHandler
implements|,
name|SolrInfoMBean
block|{
comment|// statistics
comment|// TODO: should we bother synchronizing these, or is an off-by-one error
comment|// acceptable every million requests or so?
DECL|field|numRequests
name|long
name|numRequests
decl_stmt|;
DECL|field|numErrors
name|long
name|numErrors
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|SolrCore
operator|.
name|log
operator|.
name|log
argument_list|(
name|Level
operator|.
name|INFO
argument_list|,
literal|"Unused request handler arguments:"
operator|+
name|args
argument_list|)
expr_stmt|;
block|}
DECL|field|splitList
specifier|private
specifier|final
name|Pattern
name|splitList
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|",| "
argument_list|)
decl_stmt|;
DECL|method|handleRequest
specifier|public
name|void
name|handleRequest
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
name|numRequests
operator|++
expr_stmt|;
comment|// TODO: test if lucene will accept an escaped ';', otherwise
comment|// we need to un-escape them before we pass to QueryParser
try|try
block|{
name|String
name|sreq
init|=
name|req
operator|.
name|getQueryString
argument_list|()
decl_stmt|;
name|String
name|debug
init|=
name|req
operator|.
name|getParam
argument_list|(
literal|"debugQuery"
argument_list|)
decl_stmt|;
comment|// find fieldnames to return (fieldlist)
name|String
name|fl
init|=
name|req
operator|.
name|getParam
argument_list|(
literal|"fl"
argument_list|)
decl_stmt|;
name|int
name|flags
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|fl
operator|!=
literal|null
condition|)
block|{
comment|// TODO - this could become more efficient if widely used.
comment|// TODO - should field order be maintained?
name|String
index|[]
name|flst
init|=
name|splitList
operator|.
name|split
argument_list|(
name|fl
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|flst
operator|.
name|length
operator|>
literal|0
operator|&&
operator|!
operator|(
name|flst
operator|.
name|length
operator|==
literal|1
operator|&&
name|flst
index|[
literal|0
index|]
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|)
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|fname
range|:
name|flst
control|)
block|{
if|if
condition|(
literal|"score"
operator|.
name|equals
argument_list|(
name|fname
argument_list|)
condition|)
name|flags
operator||=
name|SolrIndexSearcher
operator|.
name|GET_SCORES
expr_stmt|;
name|set
operator|.
name|add
argument_list|(
name|fname
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|setReturnFields
argument_list|(
name|set
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|sreq
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
literal|400
argument_list|,
literal|"Missing queryString"
argument_list|)
throw|;
name|List
argument_list|<
name|String
argument_list|>
name|commands
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|sreq
argument_list|,
literal|';'
argument_list|)
decl_stmt|;
name|String
name|qs
init|=
name|commands
operator|.
name|size
argument_list|()
operator|>=
literal|1
condition|?
name|commands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
else|:
literal|""
decl_stmt|;
name|Query
name|query
init|=
name|QueryParsing
operator|.
name|parseQuery
argument_list|(
name|qs
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
argument_list|)
decl_stmt|;
comment|// If the first non-query, non-filter command is a simple sort on an indexed field, then
comment|// we can use the Lucene sort ability.
name|Sort
name|sort
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|commands
operator|.
name|size
argument_list|()
operator|>=
literal|2
condition|)
block|{
name|QueryParsing
operator|.
name|SortSpec
name|sortSpec
init|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
name|commands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|sortSpec
operator|!=
literal|null
condition|)
block|{
name|sort
operator|=
name|sortSpec
operator|.
name|getSort
argument_list|()
expr_stmt|;
comment|// ignore the count for now... it's currently only controlled by start& limit on req
comment|// count = sortSpec.getCount();
block|}
block|}
name|DocList
name|results
init|=
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getDocList
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
name|sort
argument_list|,
name|req
operator|.
name|getStart
argument_list|()
argument_list|,
name|req
operator|.
name|getLimit
argument_list|()
argument_list|,
name|flags
argument_list|)
decl_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|null
argument_list|,
name|results
argument_list|)
expr_stmt|;
if|if
condition|(
name|debug
operator|!=
literal|null
condition|)
block|{
name|NamedList
name|dbg
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
try|try
block|{
name|dbg
operator|.
name|add
argument_list|(
literal|"querystring"
argument_list|,
name|qs
argument_list|)
expr_stmt|;
name|dbg
operator|.
name|add
argument_list|(
literal|"parsedquery"
argument_list|,
name|QueryParsing
operator|.
name|toString
argument_list|(
name|query
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|dbg
operator|.
name|add
argument_list|(
literal|"explain"
argument_list|,
name|getExplainList
argument_list|(
name|query
argument_list|,
name|results
argument_list|,
name|req
operator|.
name|getSearcher
argument_list|()
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|otherQueryS
init|=
name|req
operator|.
name|getParam
argument_list|(
literal|"explainOther"
argument_list|)
decl_stmt|;
if|if
condition|(
name|otherQueryS
operator|!=
literal|null
operator|&&
name|otherQueryS
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|DocList
name|otherResults
init|=
name|doQuery
argument_list|(
name|otherQueryS
argument_list|,
name|req
operator|.
name|getSearcher
argument_list|()
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|dbg
operator|.
name|add
argument_list|(
literal|"otherQuery"
argument_list|,
name|otherQueryS
argument_list|)
expr_stmt|;
name|dbg
operator|.
name|add
argument_list|(
literal|"explainOther"
argument_list|,
name|getExplainList
argument_list|(
name|query
argument_list|,
name|otherResults
argument_list|,
name|req
operator|.
name|getSearcher
argument_list|()
argument_list|,
name|req
operator|.
name|getSchema
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|logOnce
argument_list|(
name|SolrCore
operator|.
name|log
argument_list|,
literal|"Exception during debug:"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|dbg
operator|.
name|add
argument_list|(
literal|"exception_during_debug"
argument_list|,
name|SolrException
operator|.
name|toStr
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|add
argument_list|(
literal|"debug"
argument_list|,
name|dbg
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|rsp
operator|.
name|setException
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|numErrors
operator|++
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|SolrCore
operator|.
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|numErrors
operator|++
expr_stmt|;
return|return;
block|}
block|}
DECL|method|getExplainList
specifier|private
name|NamedList
name|getExplainList
parameter_list|(
name|Query
name|query
parameter_list|,
name|DocList
name|results
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|IndexSchema
name|schema
parameter_list|)
throws|throws
name|IOException
block|{
name|NamedList
name|explainList
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|DocIterator
name|iterator
init|=
name|results
operator|.
name|iterator
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
name|results
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|int
name|id
init|=
name|iterator
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|Explanation
name|explain
init|=
name|searcher
operator|.
name|explain
argument_list|(
name|query
argument_list|,
name|id
argument_list|)
decl_stmt|;
comment|//explainList.add(Integer.toString(id), explain.toString().split("\n"));
name|Document
name|doc
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|String
name|strid
init|=
name|schema
operator|.
name|printableUniqueKey
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|String
name|docname
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|strid
operator|!=
literal|null
condition|)
name|docname
operator|=
literal|"id="
operator|+
name|strid
operator|+
literal|","
expr_stmt|;
name|docname
operator|=
name|docname
operator|+
literal|"internal_docid="
operator|+
name|id
expr_stmt|;
name|explainList
operator|.
name|add
argument_list|(
name|docname
argument_list|,
literal|"\n"
operator|+
name|explain
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|explainList
return|;
block|}
DECL|method|doQuery
specifier|private
name|DocList
name|doQuery
parameter_list|(
name|String
name|sreq
parameter_list|,
name|SolrIndexSearcher
name|searcher
parameter_list|,
name|IndexSchema
name|schema
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|limit
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|commands
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|sreq
argument_list|,
literal|';'
argument_list|)
decl_stmt|;
name|String
name|qs
init|=
name|commands
operator|.
name|size
argument_list|()
operator|>=
literal|1
condition|?
name|commands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
else|:
literal|""
decl_stmt|;
name|Query
name|query
init|=
name|QueryParsing
operator|.
name|parseQuery
argument_list|(
name|qs
argument_list|,
name|schema
argument_list|)
decl_stmt|;
comment|// If the first non-query, non-filter command is a simple sort on an indexed field, then
comment|// we can use the Lucene sort ability.
name|Sort
name|sort
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|commands
operator|.
name|size
argument_list|()
operator|>=
literal|2
condition|)
block|{
name|QueryParsing
operator|.
name|SortSpec
name|sortSpec
init|=
name|QueryParsing
operator|.
name|parseSort
argument_list|(
name|commands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|schema
argument_list|)
decl_stmt|;
if|if
condition|(
name|sortSpec
operator|!=
literal|null
condition|)
block|{
name|sort
operator|=
name|sortSpec
operator|.
name|getSort
argument_list|()
expr_stmt|;
if|if
condition|(
name|sortSpec
operator|.
name|getCount
argument_list|()
operator|>=
literal|0
condition|)
block|{
name|limit
operator|=
name|sortSpec
operator|.
name|getCount
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|DocList
name|results
init|=
name|searcher
operator|.
name|getDocList
argument_list|(
name|query
argument_list|,
operator|(
name|DocSet
operator|)
literal|null
argument_list|,
name|sort
argument_list|,
name|start
argument_list|,
name|limit
argument_list|)
decl_stmt|;
return|return
name|results
return|;
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|StandardRequestHandler
operator|.
name|class
operator|.
name|getName
argument_list|()
return|;
block|}
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|SolrCore
operator|.
name|version
return|;
block|}
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"The standard Solr request handler"
return|;
block|}
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|Category
operator|.
name|QUERYHANDLER
return|;
block|}
DECL|method|getCvsId
specifier|public
name|String
name|getCvsId
parameter_list|()
block|{
return|return
literal|"$Id: StandardRequestHandler.java,v 1.17 2005/12/02 04:31:06 yonik Exp $"
return|;
block|}
DECL|method|getCvsName
specifier|public
name|String
name|getCvsName
parameter_list|()
block|{
return|return
literal|"$Name:  $"
return|;
block|}
DECL|method|getCvsSource
specifier|public
name|String
name|getCvsSource
parameter_list|()
block|{
return|return
literal|"$Source: /cvs/main/searching/solr/solarcore/src/solr/StandardRequestHandler.java,v $"
return|;
block|}
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|getStatistics
specifier|public
name|NamedList
name|getStatistics
parameter_list|()
block|{
name|NamedList
name|lst
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"requests"
argument_list|,
name|numRequests
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"errors"
argument_list|,
name|numErrors
argument_list|)
expr_stmt|;
return|return
name|lst
return|;
block|}
block|}
end_class

end_unit


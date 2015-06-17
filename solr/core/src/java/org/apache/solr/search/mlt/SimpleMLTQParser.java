begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search.mlt
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|mlt
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
name|queries
operator|.
name|mlt
operator|.
name|MoreLikeThis
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
name|TermQuery
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
name|util
operator|.
name|BytesRefBuilder
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
name|NumericUtils
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
name|params
operator|.
name|SolrParams
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
name|request
operator|.
name|SolrQueryRequest
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
name|QParser
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
name|QueryParsing
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
name|Map
import|;
end_import

begin_class
DECL|class|SimpleMLTQParser
specifier|public
class|class
name|SimpleMLTQParser
extends|extends
name|QParser
block|{
DECL|method|SimpleMLTQParser
specifier|public
name|SimpleMLTQParser
parameter_list|(
name|String
name|qstr
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|super
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|()
block|{
name|String
name|defaultField
init|=
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getUniqueKeyField
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|uniqueValue
init|=
name|localParams
operator|.
name|get
argument_list|(
name|QueryParsing
operator|.
name|V
argument_list|)
decl_stmt|;
name|String
index|[]
name|qf
init|=
name|localParams
operator|.
name|getParams
argument_list|(
literal|"qf"
argument_list|)
decl_stmt|;
name|SolrIndexSearcher
name|searcher
init|=
name|req
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|Query
name|docIdQuery
init|=
name|createIdQuery
argument_list|(
name|defaultField
argument_list|,
name|uniqueValue
argument_list|)
decl_stmt|;
try|try
block|{
name|TopDocs
name|td
init|=
name|searcher
operator|.
name|search
argument_list|(
name|docIdQuery
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|td
operator|.
name|totalHits
operator|!=
literal|1
condition|)
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
literal|"Error completing MLT request. Could not fetch "
operator|+
literal|"document with id ["
operator|+
name|uniqueValue
operator|+
literal|"]"
argument_list|)
throw|;
name|ScoreDoc
index|[]
name|scoreDocs
init|=
name|td
operator|.
name|scoreDocs
decl_stmt|;
name|MoreLikeThis
name|mlt
init|=
operator|new
name|MoreLikeThis
argument_list|(
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|localParams
operator|.
name|getInt
argument_list|(
literal|"mintf"
argument_list|)
operator|!=
literal|null
condition|)
name|mlt
operator|.
name|setMinTermFreq
argument_list|(
name|localParams
operator|.
name|getInt
argument_list|(
literal|"mintf"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|localParams
operator|.
name|getInt
argument_list|(
literal|"mindf"
argument_list|)
operator|!=
literal|null
condition|)
name|mlt
operator|.
name|setMinDocFreq
argument_list|(
name|localParams
operator|.
name|getInt
argument_list|(
literal|"mindf"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|localParams
operator|.
name|get
argument_list|(
literal|"minwl"
argument_list|)
operator|!=
literal|null
condition|)
name|mlt
operator|.
name|setMinWordLen
argument_list|(
name|localParams
operator|.
name|getInt
argument_list|(
literal|"minwl"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|localParams
operator|.
name|get
argument_list|(
literal|"maxwl"
argument_list|)
operator|!=
literal|null
condition|)
name|mlt
operator|.
name|setMaxWordLen
argument_list|(
name|localParams
operator|.
name|getInt
argument_list|(
literal|"maxwl"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|localParams
operator|.
name|get
argument_list|(
literal|"maxqt"
argument_list|)
operator|!=
literal|null
condition|)
name|mlt
operator|.
name|setMaxWordLen
argument_list|(
name|localParams
operator|.
name|getInt
argument_list|(
literal|"maxqt"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|localParams
operator|.
name|get
argument_list|(
literal|"maxntp"
argument_list|)
operator|!=
literal|null
condition|)
name|mlt
operator|.
name|setMaxWordLen
argument_list|(
name|localParams
operator|.
name|getInt
argument_list|(
literal|"maxntp"
argument_list|)
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
if|if
condition|(
name|qf
operator|!=
literal|null
condition|)
block|{
name|mlt
operator|.
name|setFieldNames
argument_list|(
name|qf
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|SchemaField
argument_list|>
name|fieldNames
init|=
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getFields
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|fieldName
range|:
name|fieldNames
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|fieldNames
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
operator|.
name|indexed
argument_list|()
operator|&&
name|fieldNames
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
operator|.
name|stored
argument_list|()
condition|)
if|if
condition|(
name|fieldNames
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
operator|.
name|getType
argument_list|()
operator|.
name|getNumericType
argument_list|()
operator|==
literal|null
condition|)
name|fields
operator|.
name|add
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
name|mlt
operator|.
name|setFieldNames
argument_list|(
name|fields
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|fields
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|mlt
operator|.
name|setAnalyzer
argument_list|(
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getIndexAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|mlt
operator|.
name|like
argument_list|(
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
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
literal|"Error completing MLT request"
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|createIdQuery
specifier|private
name|Query
name|createIdQuery
parameter_list|(
name|String
name|defaultField
parameter_list|,
name|String
name|uniqueValue
parameter_list|)
block|{
return|return
operator|new
name|TermQuery
argument_list|(
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|defaultField
argument_list|)
operator|.
name|getType
argument_list|()
operator|.
name|getNumericType
argument_list|()
operator|!=
literal|null
condition|?
name|createNumericTerm
argument_list|(
name|defaultField
argument_list|,
name|uniqueValue
argument_list|)
else|:
operator|new
name|Term
argument_list|(
name|defaultField
argument_list|,
name|uniqueValue
argument_list|)
argument_list|)
return|;
block|}
DECL|method|createNumericTerm
specifier|private
name|Term
name|createNumericTerm
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|uniqueValue
parameter_list|)
block|{
name|BytesRefBuilder
name|bytesRefBuilder
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
name|bytesRefBuilder
operator|.
name|grow
argument_list|(
name|NumericUtils
operator|.
name|BUF_SIZE_INT
argument_list|)
expr_stmt|;
name|NumericUtils
operator|.
name|intToPrefixCoded
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|uniqueValue
argument_list|)
argument_list|,
literal|0
argument_list|,
name|bytesRefBuilder
argument_list|)
expr_stmt|;
return|return
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|bytesRefBuilder
operator|.
name|toBytesRef
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit


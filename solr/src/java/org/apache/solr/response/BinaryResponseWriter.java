begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
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
name|Fieldable
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
name|SolrDocument
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
name|SolrDocumentList
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
name|CommonParams
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
name|common
operator|.
name|util
operator|.
name|JavaBinCodec
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
name|response
operator|.
name|transform
operator|.
name|DocTransformer
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
name|response
operator|.
name|transform
operator|.
name|TransformContext
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
name|search
operator|.
name|DocList
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
name|ReturnFields
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_class
DECL|class|BinaryResponseWriter
specifier|public
class|class
name|BinaryResponseWriter
implements|implements
name|BinaryQueryResponseWriter
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BinaryResponseWriter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|KNOWN_TYPES
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|Class
argument_list|>
name|KNOWN_TYPES
init|=
operator|new
name|HashSet
argument_list|<
name|Class
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|Resolver
name|resolver
init|=
operator|new
name|Resolver
argument_list|(
name|req
argument_list|,
name|response
operator|.
name|getReturnFields
argument_list|()
argument_list|)
decl_stmt|;
name|Boolean
name|omitHeader
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
name|CommonParams
operator|.
name|OMIT_HEADER
argument_list|)
decl_stmt|;
if|if
condition|(
name|omitHeader
operator|!=
literal|null
operator|&&
name|omitHeader
condition|)
name|response
operator|.
name|getValues
argument_list|()
operator|.
name|remove
argument_list|(
literal|"responseHeader"
argument_list|)
expr_stmt|;
name|JavaBinCodec
name|codec
init|=
operator|new
name|JavaBinCodec
argument_list|(
name|resolver
argument_list|)
decl_stmt|;
name|codec
operator|.
name|marshal
argument_list|(
name|response
operator|.
name|getValues
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"This is a binary writer , Cannot write to a characterstream"
argument_list|)
throw|;
block|}
DECL|method|getContentType
specifier|public
name|String
name|getContentType
parameter_list|(
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
block|{
return|return
literal|"application/octet-stream"
return|;
block|}
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
comment|/* NOOP */
block|}
DECL|class|Resolver
specifier|public
specifier|static
class|class
name|Resolver
implements|implements
name|JavaBinCodec
operator|.
name|ObjectResolver
block|{
DECL|field|solrQueryRequest
specifier|protected
specifier|final
name|SolrQueryRequest
name|solrQueryRequest
decl_stmt|;
DECL|field|schema
specifier|protected
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|searcher
specifier|protected
name|SolrIndexSearcher
name|searcher
decl_stmt|;
DECL|field|returnFields
specifier|protected
specifier|final
name|ReturnFields
name|returnFields
decl_stmt|;
comment|// transmit field values using FieldType.toObject()
comment|// rather than the String from FieldType.toExternal()
DECL|field|useFieldObjects
name|boolean
name|useFieldObjects
init|=
literal|true
decl_stmt|;
DECL|method|Resolver
specifier|public
name|Resolver
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|ReturnFields
name|returnFields
parameter_list|)
block|{
name|solrQueryRequest
operator|=
name|req
expr_stmt|;
name|this
operator|.
name|returnFields
operator|=
name|returnFields
expr_stmt|;
block|}
DECL|method|resolve
specifier|public
name|Object
name|resolve
parameter_list|(
name|Object
name|o
parameter_list|,
name|JavaBinCodec
name|codec
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|o
operator|instanceof
name|ResultContext
condition|)
block|{
name|writeResults
argument_list|(
operator|(
name|ResultContext
operator|)
name|o
argument_list|,
name|codec
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
comment|// null means we completely handled it
block|}
if|if
condition|(
name|o
operator|instanceof
name|DocList
condition|)
block|{
name|ResultContext
name|ctx
init|=
operator|new
name|ResultContext
argument_list|()
decl_stmt|;
name|ctx
operator|.
name|docs
operator|=
operator|(
name|DocList
operator|)
name|o
expr_stmt|;
name|writeResults
argument_list|(
name|ctx
argument_list|,
name|codec
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
comment|// null means we completely handled it
block|}
if|if
condition|(
name|o
operator|instanceof
name|SolrDocument
condition|)
block|{
comment|// Remove any fields that were not requested
comment|// This typically happens when distributed search adds extra fields to an internal request
name|SolrDocument
name|doc
init|=
operator|(
name|SolrDocument
operator|)
name|o
decl_stmt|;
for|for
control|(
name|String
name|fname
range|:
name|doc
operator|.
name|getFieldNames
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|returnFields
operator|.
name|wantsField
argument_list|(
name|fname
argument_list|)
condition|)
block|{
name|doc
operator|.
name|removeFields
argument_list|(
name|fname
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|doc
return|;
block|}
return|return
name|o
return|;
block|}
DECL|method|writeResultsBody
specifier|protected
name|void
name|writeResultsBody
parameter_list|(
name|ResultContext
name|res
parameter_list|,
name|JavaBinCodec
name|codec
parameter_list|)
throws|throws
name|IOException
block|{
name|DocList
name|ids
init|=
name|res
operator|.
name|docs
decl_stmt|;
name|TransformContext
name|context
init|=
operator|new
name|TransformContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|query
operator|=
name|res
operator|.
name|query
expr_stmt|;
name|context
operator|.
name|wantsScores
operator|=
name|returnFields
operator|.
name|wantsScore
argument_list|()
operator|&&
name|ids
operator|.
name|hasScores
argument_list|()
expr_stmt|;
name|int
name|sz
init|=
name|ids
operator|.
name|size
argument_list|()
decl_stmt|;
name|codec
operator|.
name|writeTag
argument_list|(
name|JavaBinCodec
operator|.
name|ARR
argument_list|,
name|sz
argument_list|)
expr_stmt|;
if|if
condition|(
name|searcher
operator|==
literal|null
condition|)
name|searcher
operator|=
name|solrQueryRequest
operator|.
name|getSearcher
argument_list|()
expr_stmt|;
if|if
condition|(
name|schema
operator|==
literal|null
condition|)
name|schema
operator|=
name|solrQueryRequest
operator|.
name|getSchema
argument_list|()
expr_stmt|;
name|context
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
name|DocTransformer
name|transformer
init|=
name|returnFields
operator|.
name|getTransformer
argument_list|()
decl_stmt|;
if|if
condition|(
name|transformer
operator|!=
literal|null
condition|)
block|{
name|transformer
operator|.
name|setContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|fnames
init|=
name|returnFields
operator|.
name|getLuceneFieldNames
argument_list|()
decl_stmt|;
name|context
operator|.
name|iterator
operator|=
name|ids
operator|.
name|iterator
argument_list|()
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
name|sz
condition|;
name|i
operator|++
control|)
block|{
name|int
name|id
init|=
name|context
operator|.
name|iterator
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|id
argument_list|,
name|fnames
argument_list|)
decl_stmt|;
name|SolrDocument
name|sdoc
init|=
name|getDoc
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|transformer
operator|!=
literal|null
condition|)
block|{
name|transformer
operator|.
name|transform
argument_list|(
name|sdoc
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
name|codec
operator|.
name|writeSolrDocument
argument_list|(
name|sdoc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|transformer
operator|!=
literal|null
condition|)
block|{
name|transformer
operator|.
name|setContext
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeResults
specifier|public
name|void
name|writeResults
parameter_list|(
name|ResultContext
name|ctx
parameter_list|,
name|JavaBinCodec
name|codec
parameter_list|)
throws|throws
name|IOException
block|{
name|codec
operator|.
name|writeTag
argument_list|(
name|JavaBinCodec
operator|.
name|SOLRDOCLST
argument_list|)
expr_stmt|;
name|boolean
name|wantsScores
init|=
name|returnFields
operator|.
name|wantsScore
argument_list|()
operator|&&
name|ctx
operator|.
name|docs
operator|.
name|hasScores
argument_list|()
decl_stmt|;
name|List
name|l
init|=
operator|new
name|ArrayList
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
operator|(
name|long
operator|)
name|ctx
operator|.
name|docs
operator|.
name|matches
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
operator|(
name|long
operator|)
name|ctx
operator|.
name|docs
operator|.
name|offset
argument_list|()
argument_list|)
expr_stmt|;
name|Float
name|maxScore
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|wantsScores
condition|)
block|{
name|maxScore
operator|=
name|ctx
operator|.
name|docs
operator|.
name|maxScore
argument_list|()
expr_stmt|;
block|}
name|l
operator|.
name|add
argument_list|(
name|maxScore
argument_list|)
expr_stmt|;
name|codec
operator|.
name|writeArray
argument_list|(
name|l
argument_list|)
expr_stmt|;
comment|// this is a seprate function so that streaming responses can use just that part
name|writeResultsBody
argument_list|(
name|ctx
argument_list|,
name|codec
argument_list|)
expr_stmt|;
block|}
DECL|method|getDoc
specifier|public
name|SolrDocument
name|getDoc
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
name|SolrDocument
name|solrDoc
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
for|for
control|(
name|Fieldable
name|f
range|:
name|doc
operator|.
name|getFields
argument_list|()
control|)
block|{
name|String
name|fieldName
init|=
name|f
operator|.
name|name
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|returnFields
operator|.
name|wantsField
argument_list|(
name|fieldName
argument_list|)
condition|)
continue|continue;
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|FieldType
name|ft
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|sf
operator|!=
literal|null
condition|)
name|ft
operator|=
name|sf
operator|.
name|getType
argument_list|()
expr_stmt|;
name|Object
name|val
decl_stmt|;
if|if
condition|(
name|ft
operator|==
literal|null
condition|)
block|{
comment|// handle fields not in the schema
if|if
condition|(
name|f
operator|.
name|isBinary
argument_list|()
condition|)
name|val
operator|=
name|f
operator|.
name|getBinaryValue
argument_list|()
expr_stmt|;
else|else
name|val
operator|=
name|f
operator|.
name|stringValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
if|if
condition|(
name|useFieldObjects
operator|&&
name|KNOWN_TYPES
operator|.
name|contains
argument_list|(
name|ft
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
name|val
operator|=
name|ft
operator|.
name|toObject
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|val
operator|=
name|ft
operator|.
name|toExternal
argument_list|(
name|f
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
comment|// There is a chance of the underlying field not really matching the
comment|// actual field type . So ,it can throw exception
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error reading a field from document : "
operator|+
name|solrDoc
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|//if it happens log it and continue
continue|continue;
block|}
block|}
if|if
condition|(
name|sf
operator|!=
literal|null
operator|&&
name|sf
operator|.
name|multiValued
argument_list|()
operator|&&
operator|!
name|solrDoc
operator|.
name|containsKey
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|ArrayList
name|l
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|solrDoc
operator|.
name|addField
argument_list|(
name|fieldName
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|solrDoc
operator|.
name|addField
argument_list|(
name|fieldName
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|solrDoc
return|;
block|}
block|}
comment|/**    * TODO -- there may be a way to do this without marshal at all...    *    * @param req    * @param rsp    *    * @return a response object equivalent to what you get from the XML/JSON/javabin parser. Documents become    *         SolrDocuments, DocList becomes SolrDocumentList etc.    *    * @since solr 1.4    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getParsedResponse
specifier|public
specifier|static
name|NamedList
argument_list|<
name|Object
argument_list|>
name|getParsedResponse
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
try|try
block|{
name|Resolver
name|resolver
init|=
operator|new
name|Resolver
argument_list|(
name|req
argument_list|,
name|rsp
operator|.
name|getReturnFields
argument_list|()
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
operator|new
name|JavaBinCodec
argument_list|(
name|resolver
argument_list|)
operator|.
name|marshal
argument_list|(
name|rsp
operator|.
name|getValues
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|InputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
operator|new
name|JavaBinCodec
argument_list|(
name|resolver
argument_list|)
operator|.
name|unmarshal
argument_list|(
name|in
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
static|static
block|{
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|BoolField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|BCDIntField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|BCDLongField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|BCDStrField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|ByteField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|DateField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|DoubleField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|FloatField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|ShortField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|IntField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|LongField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|SortableLongField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|SortableIntField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|SortableFloatField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|SortableDoubleField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|StrField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|TextField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|TrieField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|TrieIntField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|TrieLongField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|TrieFloatField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|TrieDoubleField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|TrieDateField
operator|.
name|class
argument_list|)
expr_stmt|;
name|KNOWN_TYPES
operator|.
name|add
argument_list|(
name|BinaryField
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// We do not add UUIDField because UUID object is not a supported type in JavaBinCodec
comment|// and if we write UUIDField.toObject, we wouldn't know how to handle it in the client side
block|}
block|}
end_class

end_unit


begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Writer
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
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|StorableField
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
name|StoredDocument
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|Tuple
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
name|io
operator|.
name|stream
operator|.
name|TupleStream
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
name|EnumFieldValue
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
name|util
operator|.
name|Base64
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
name|util
operator|.
name|DateFormatUtil
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
name|FastWriter
import|;
end_import

begin_comment
comment|/** Base class for text-oriented response writers.  *  *  */
end_comment

begin_class
DECL|class|TextResponseWriter
specifier|public
specifier|abstract
class|class
name|TextResponseWriter
block|{
comment|// indent up to 40 spaces
DECL|field|indentChars
specifier|static
specifier|final
name|char
index|[]
name|indentChars
init|=
operator|new
name|char
index|[
literal|81
index|]
decl_stmt|;
static|static
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|indentChars
argument_list|,
literal|' '
argument_list|)
expr_stmt|;
name|indentChars
index|[
literal|0
index|]
operator|=
literal|'\n'
expr_stmt|;
comment|// start with a newline
block|}
DECL|field|writer
specifier|protected
specifier|final
name|FastWriter
name|writer
decl_stmt|;
DECL|field|schema
specifier|protected
specifier|final
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|req
specifier|protected
specifier|final
name|SolrQueryRequest
name|req
decl_stmt|;
DECL|field|rsp
specifier|protected
specifier|final
name|SolrQueryResponse
name|rsp
decl_stmt|;
comment|// the default set of fields to return for each document
DECL|field|returnFields
specifier|protected
name|ReturnFields
name|returnFields
decl_stmt|;
DECL|field|level
specifier|protected
name|int
name|level
decl_stmt|;
DECL|field|doIndent
specifier|protected
name|boolean
name|doIndent
decl_stmt|;
DECL|field|cal
specifier|protected
name|Calendar
name|cal
decl_stmt|;
comment|// reusable calendar instance
DECL|method|TextResponseWriter
specifier|public
name|TextResponseWriter
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
name|this
operator|.
name|writer
operator|=
name|writer
operator|==
literal|null
condition|?
literal|null
else|:
name|FastWriter
operator|.
name|wrap
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|req
operator|.
name|getSchema
argument_list|()
expr_stmt|;
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
name|this
operator|.
name|rsp
operator|=
name|rsp
expr_stmt|;
name|String
name|indent
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"indent"
argument_list|)
decl_stmt|;
if|if
condition|(
name|indent
operator|!=
literal|null
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|indent
argument_list|)
operator|&&
operator|!
literal|"off"
operator|.
name|equals
argument_list|(
name|indent
argument_list|)
condition|)
block|{
name|doIndent
operator|=
literal|true
expr_stmt|;
block|}
name|returnFields
operator|=
name|rsp
operator|.
name|getReturnFields
argument_list|()
expr_stmt|;
block|}
comment|/** done with this ResponseWriter... make sure any buffers are flushed to writer */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
name|writer
operator|.
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
comment|/** returns the Writer that the response is being written to */
DECL|method|getWriter
specifier|public
name|Writer
name|getWriter
parameter_list|()
block|{
return|return
name|writer
return|;
block|}
DECL|method|indent
specifier|public
name|void
name|indent
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|doIndent
condition|)
name|indent
argument_list|(
name|level
argument_list|)
expr_stmt|;
block|}
DECL|method|indent
specifier|public
name|void
name|indent
parameter_list|(
name|int
name|lev
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|write
argument_list|(
name|indentChars
argument_list|,
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
operator|(
name|lev
operator|<<
literal|1
operator|)
operator|+
literal|1
argument_list|,
name|indentChars
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//
comment|// Functions to manipulate the current logical nesting level.
comment|// Any indentation will be partially based on level.
comment|//
DECL|method|setLevel
specifier|public
name|void
name|setLevel
parameter_list|(
name|int
name|level
parameter_list|)
block|{
name|this
operator|.
name|level
operator|=
name|level
expr_stmt|;
block|}
DECL|method|level
specifier|public
name|int
name|level
parameter_list|()
block|{
return|return
name|level
return|;
block|}
DECL|method|incLevel
specifier|public
name|int
name|incLevel
parameter_list|()
block|{
return|return
operator|++
name|level
return|;
block|}
DECL|method|decLevel
specifier|public
name|int
name|decLevel
parameter_list|()
block|{
return|return
operator|--
name|level
return|;
block|}
DECL|method|setIndent
specifier|public
name|void
name|setIndent
parameter_list|(
name|boolean
name|doIndent
parameter_list|)
block|{
name|this
operator|.
name|doIndent
operator|=
name|doIndent
expr_stmt|;
block|}
DECL|method|writeNamedList
specifier|public
specifier|abstract
name|void
name|writeNamedList
parameter_list|(
name|String
name|name
parameter_list|,
name|NamedList
name|val
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeVal
specifier|public
specifier|final
name|void
name|writeVal
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|val
parameter_list|)
throws|throws
name|IOException
block|{
comment|// if there get to be enough types, perhaps hashing on the type
comment|// to get a handler might be faster (but types must be exact to do that...)
comment|// go in order of most common to least common
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
name|writeNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|String
condition|)
block|{
name|writeStr
argument_list|(
name|name
argument_list|,
name|val
operator|.
name|toString
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// micro-optimization... using toString() avoids a cast first
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|StorableField
condition|)
block|{
name|StorableField
name|f
init|=
operator|(
name|StorableField
operator|)
name|val
decl_stmt|;
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getFieldOrNull
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|sf
operator|!=
literal|null
condition|)
block|{
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|write
argument_list|(
name|this
argument_list|,
name|name
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeStr
argument_list|(
name|name
argument_list|,
name|f
operator|.
name|stringValue
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Number
condition|)
block|{
name|writeNumber
argument_list|(
name|name
argument_list|,
operator|(
name|Number
operator|)
name|val
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Boolean
condition|)
block|{
name|writeBool
argument_list|(
name|name
argument_list|,
operator|(
name|Boolean
operator|)
name|val
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Date
condition|)
block|{
name|writeDate
argument_list|(
name|name
argument_list|,
operator|(
name|Date
operator|)
name|val
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|StoredDocument
condition|)
block|{
name|SolrDocument
name|doc
init|=
name|DocsStreamer
operator|.
name|getDoc
argument_list|(
operator|(
name|StoredDocument
operator|)
name|val
argument_list|,
name|schema
argument_list|)
decl_stmt|;
name|writeSolrDocument
argument_list|(
name|name
argument_list|,
name|doc
argument_list|,
name|returnFields
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|SolrDocument
condition|)
block|{
name|writeSolrDocument
argument_list|(
name|name
argument_list|,
operator|(
name|SolrDocument
operator|)
name|val
argument_list|,
name|returnFields
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|ResultContext
condition|)
block|{
comment|// requires access to IndexReader
name|writeDocuments
argument_list|(
name|name
argument_list|,
operator|(
name|ResultContext
operator|)
name|val
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|DocList
condition|)
block|{
comment|// Should not happen normally
name|ResultContext
name|ctx
init|=
operator|new
name|BasicResultContext
argument_list|(
operator|(
name|DocList
operator|)
name|val
argument_list|,
name|returnFields
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|writeDocuments
argument_list|(
name|name
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
comment|// }
comment|// else if (val instanceof DocSet) {
comment|// how do we know what fields to read?
comment|// todo: have a DocList/DocSet wrapper that
comment|// restricts the fields to write...?
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|SolrDocumentList
condition|)
block|{
name|writeSolrDocumentList
argument_list|(
name|name
argument_list|,
operator|(
name|SolrDocumentList
operator|)
name|val
argument_list|,
name|returnFields
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Map
condition|)
block|{
name|writeMap
argument_list|(
name|name
argument_list|,
operator|(
name|Map
operator|)
name|val
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|NamedList
condition|)
block|{
name|writeNamedList
argument_list|(
name|name
argument_list|,
operator|(
name|NamedList
operator|)
name|val
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|TupleStream
condition|)
block|{
name|writeTupleStream
argument_list|(
operator|(
name|TupleStream
operator|)
name|val
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Path
condition|)
block|{
name|writeStr
argument_list|(
name|name
argument_list|,
operator|(
operator|(
name|Path
operator|)
name|val
operator|)
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Iterable
condition|)
block|{
name|writeArray
argument_list|(
name|name
argument_list|,
operator|(
operator|(
name|Iterable
operator|)
name|val
operator|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Object
index|[]
condition|)
block|{
name|writeArray
argument_list|(
name|name
argument_list|,
operator|(
name|Object
index|[]
operator|)
name|val
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Iterator
condition|)
block|{
name|writeArray
argument_list|(
name|name
argument_list|,
operator|(
name|Iterator
operator|)
name|val
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|byte
index|[]
condition|)
block|{
name|byte
index|[]
name|arr
init|=
operator|(
name|byte
index|[]
operator|)
name|val
decl_stmt|;
name|writeByteArr
argument_list|(
name|name
argument_list|,
name|arr
argument_list|,
literal|0
argument_list|,
name|arr
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|BytesRef
condition|)
block|{
name|BytesRef
name|arr
init|=
operator|(
name|BytesRef
operator|)
name|val
decl_stmt|;
name|writeByteArr
argument_list|(
name|name
argument_list|,
name|arr
operator|.
name|bytes
argument_list|,
name|arr
operator|.
name|offset
argument_list|,
name|arr
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|EnumFieldValue
condition|)
block|{
name|writeStr
argument_list|(
name|name
argument_list|,
name|val
operator|.
name|toString
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|WriteableValue
condition|)
block|{
operator|(
operator|(
name|WriteableValue
operator|)
name|val
operator|)
operator|.
name|write
argument_list|(
name|name
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// default... for debugging only
name|writeStr
argument_list|(
name|name
argument_list|,
name|val
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|':'
operator|+
name|val
operator|.
name|toString
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeBool
specifier|protected
name|void
name|writeBool
parameter_list|(
name|String
name|name
parameter_list|,
name|Boolean
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writeBool
argument_list|(
name|name
argument_list|,
name|val
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|writeNumber
specifier|protected
name|void
name|writeNumber
parameter_list|(
name|String
name|name
parameter_list|,
name|Number
name|val
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|val
operator|instanceof
name|Integer
condition|)
block|{
name|writeInt
argument_list|(
name|name
argument_list|,
name|val
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Long
condition|)
block|{
name|writeLong
argument_list|(
name|name
argument_list|,
name|val
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Float
condition|)
block|{
comment|// we pass the float instead of using toString() because
comment|// it may need special formatting. same for double.
name|writeFloat
argument_list|(
name|name
argument_list|,
operator|(
operator|(
name|Float
operator|)
name|val
operator|)
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Double
condition|)
block|{
name|writeDouble
argument_list|(
name|name
argument_list|,
operator|(
operator|(
name|Double
operator|)
name|val
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Short
condition|)
block|{
name|writeInt
argument_list|(
name|name
argument_list|,
name|val
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|instanceof
name|Byte
condition|)
block|{
name|writeInt
argument_list|(
name|name
argument_list|,
name|val
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// default... for debugging only
name|writeStr
argument_list|(
name|name
argument_list|,
name|val
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|':'
operator|+
name|val
operator|.
name|toString
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|// names are passed when writing primitives like writeInt to allow many different
comment|// types of formats, including those where the name may come after the value (like
comment|// some XML formats).
DECL|method|writeStartDocumentList
specifier|public
specifier|abstract
name|void
name|writeStartDocumentList
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|start
parameter_list|,
name|int
name|size
parameter_list|,
name|long
name|numFound
parameter_list|,
name|Float
name|maxScore
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeSolrDocument
specifier|public
specifier|abstract
name|void
name|writeSolrDocument
parameter_list|(
name|String
name|name
parameter_list|,
name|SolrDocument
name|doc
parameter_list|,
name|ReturnFields
name|fields
parameter_list|,
name|int
name|idx
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeEndDocumentList
specifier|public
specifier|abstract
name|void
name|writeEndDocumentList
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|// Assume each SolrDocument is already transformed
DECL|method|writeSolrDocumentList
specifier|public
specifier|final
name|void
name|writeSolrDocumentList
parameter_list|(
name|String
name|name
parameter_list|,
name|SolrDocumentList
name|docs
parameter_list|,
name|ReturnFields
name|fields
parameter_list|)
throws|throws
name|IOException
block|{
name|writeStartDocumentList
argument_list|(
name|name
argument_list|,
name|docs
operator|.
name|getStart
argument_list|()
argument_list|,
name|docs
operator|.
name|size
argument_list|()
argument_list|,
name|docs
operator|.
name|getNumFound
argument_list|()
argument_list|,
name|docs
operator|.
name|getMaxScore
argument_list|()
argument_list|)
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
name|docs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|writeSolrDocument
argument_list|(
literal|null
argument_list|,
name|docs
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|fields
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|writeEndDocumentList
argument_list|()
expr_stmt|;
block|}
DECL|method|writeDocuments
specifier|public
specifier|final
name|void
name|writeDocuments
parameter_list|(
name|String
name|name
parameter_list|,
name|ResultContext
name|res
parameter_list|)
throws|throws
name|IOException
block|{
name|DocList
name|ids
init|=
name|res
operator|.
name|getDocList
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|SolrDocument
argument_list|>
name|docsStreamer
init|=
name|res
operator|.
name|getProcessedDocuments
argument_list|()
decl_stmt|;
name|writeStartDocumentList
argument_list|(
name|name
argument_list|,
name|ids
operator|.
name|offset
argument_list|()
argument_list|,
name|ids
operator|.
name|size
argument_list|()
argument_list|,
name|ids
operator|.
name|matches
argument_list|()
argument_list|,
name|res
operator|.
name|wantsScores
argument_list|()
condition|?
operator|new
name|Float
argument_list|(
name|ids
operator|.
name|maxScore
argument_list|()
argument_list|)
else|:
literal|null
argument_list|)
expr_stmt|;
name|int
name|idx
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|docsStreamer
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|writeSolrDocument
argument_list|(
literal|null
argument_list|,
name|docsStreamer
operator|.
name|next
argument_list|()
argument_list|,
name|res
operator|.
name|getReturnFields
argument_list|()
argument_list|,
name|idx
argument_list|)
expr_stmt|;
name|idx
operator|++
expr_stmt|;
block|}
name|writeEndDocumentList
argument_list|()
expr_stmt|;
block|}
DECL|method|writeStr
specifier|public
specifier|abstract
name|void
name|writeStr
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|,
name|boolean
name|needsEscaping
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeMap
specifier|public
specifier|abstract
name|void
name|writeMap
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
name|val
parameter_list|,
name|boolean
name|excludeOuter
parameter_list|,
name|boolean
name|isFirstVal
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeArray
specifier|public
name|void
name|writeArray
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
index|[]
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writeArray
argument_list|(
name|name
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|val
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|writeArray
specifier|public
specifier|abstract
name|void
name|writeArray
parameter_list|(
name|String
name|name
parameter_list|,
name|Iterator
name|val
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeNull
specifier|public
specifier|abstract
name|void
name|writeNull
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** if this form of the method is called, val is the Java string form of an int */
DECL|method|writeInt
specifier|public
specifier|abstract
name|void
name|writeInt
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeInt
specifier|public
name|void
name|writeInt
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writeInt
argument_list|(
name|name
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** if this form of the method is called, val is the Java string form of a long */
DECL|method|writeLong
specifier|public
specifier|abstract
name|void
name|writeLong
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeLong
specifier|public
name|void
name|writeLong
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writeLong
argument_list|(
name|name
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** if this form of the method is called, val is the Java string form of a boolean */
DECL|method|writeBool
specifier|public
specifier|abstract
name|void
name|writeBool
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeBool
specifier|public
name|void
name|writeBool
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writeBool
argument_list|(
name|name
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** if this form of the method is called, val is the Java string form of a float */
DECL|method|writeFloat
specifier|public
specifier|abstract
name|void
name|writeFloat
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeFloat
specifier|public
name|void
name|writeFloat
parameter_list|(
name|String
name|name
parameter_list|,
name|float
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|s
init|=
name|Float
operator|.
name|toString
argument_list|(
name|val
argument_list|)
decl_stmt|;
comment|// If it's not a normal number, write the value as a string instead.
comment|// The following test also handles NaN since comparisons are always false.
if|if
condition|(
name|val
operator|>
name|Float
operator|.
name|NEGATIVE_INFINITY
operator|&&
name|val
operator|<
name|Float
operator|.
name|POSITIVE_INFINITY
condition|)
block|{
name|writeFloat
argument_list|(
name|name
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeStr
argument_list|(
name|name
argument_list|,
name|s
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeTupleStream
specifier|public
name|void
name|writeTupleStream
parameter_list|(
name|TupleStream
name|tupleStream
parameter_list|)
throws|throws
name|IOException
block|{
name|tupleStream
operator|.
name|open
argument_list|()
expr_stmt|;
name|tupleStream
operator|.
name|writeStreamOpen
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|boolean
name|isFirst
init|=
literal|true
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Tuple
name|tuple
init|=
name|tupleStream
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isFirst
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|writeMap
argument_list|(
literal|null
argument_list|,
name|tuple
operator|.
name|fields
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|isFirst
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|tuple
operator|.
name|EOF
condition|)
block|{
break|break;
block|}
block|}
name|tupleStream
operator|.
name|writeStreamClose
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|tupleStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** if this form of the method is called, val is the Java string form of a double */
DECL|method|writeDouble
specifier|public
specifier|abstract
name|void
name|writeDouble
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeDouble
specifier|public
name|void
name|writeDouble
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|s
init|=
name|Double
operator|.
name|toString
argument_list|(
name|val
argument_list|)
decl_stmt|;
comment|// If it's not a normal number, write the value as a string instead.
comment|// The following test also handles NaN since comparisons are always false.
if|if
condition|(
name|val
operator|>
name|Double
operator|.
name|NEGATIVE_INFINITY
operator|&&
name|val
operator|<
name|Double
operator|.
name|POSITIVE_INFINITY
condition|)
block|{
name|writeDouble
argument_list|(
name|name
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeStr
argument_list|(
name|name
argument_list|,
name|s
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeDate
specifier|public
name|void
name|writeDate
parameter_list|(
name|String
name|name
parameter_list|,
name|Date
name|val
parameter_list|)
throws|throws
name|IOException
block|{
name|writeDate
argument_list|(
name|name
argument_list|,
name|DateFormatUtil
operator|.
name|formatExternal
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** if this form of the method is called, val is the Solr ISO8601 based date format */
DECL|method|writeDate
specifier|public
specifier|abstract
name|void
name|writeDate
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|writeByteArr
specifier|public
name|void
name|writeByteArr
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|writeStr
argument_list|(
name|name
argument_list|,
name|Base64
operator|.
name|byteArrayToBase64
argument_list|(
name|buf
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


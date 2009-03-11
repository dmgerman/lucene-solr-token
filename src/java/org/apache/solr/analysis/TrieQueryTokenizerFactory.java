begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
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
name|analysis
operator|.
name|Token
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|Tokenizer
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
name|trie
operator|.
name|TrieUtils
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
name|schema
operator|.
name|DateField
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
name|TrieField
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
name|io
operator|.
name|Reader
import|;
end_import

begin_comment
comment|/**  * Query time tokenizer for trie fields. It uses methods in TrieUtils to create a prefix coded representation of the  * given number which is used for term queries.  *<p/>  * Note that queries on trie date types are not tokenized and returned as is.  *  * @version $Id$  * @see org.apache.lucene.search.trie.TrieUtils  * @see org.apache.solr.schema.TrieField  * @since solr 1.4  */
end_comment

begin_class
DECL|class|TrieQueryTokenizerFactory
specifier|public
class|class
name|TrieQueryTokenizerFactory
extends|extends
name|BaseTokenizerFactory
block|{
DECL|field|type
specifier|private
specifier|final
name|TrieField
operator|.
name|TrieTypes
name|type
decl_stmt|;
DECL|method|TrieQueryTokenizerFactory
specifier|public
name|TrieQueryTokenizerFactory
parameter_list|(
name|TrieField
operator|.
name|TrieTypes
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
DECL|method|create
specifier|public
name|TokenStream
name|create
parameter_list|(
name|Reader
name|reader
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|TrieQueryTokenizer
argument_list|(
name|reader
argument_list|,
name|type
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
name|SERVER_ERROR
argument_list|,
literal|"Unable to create TrieQueryTokenizer"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|class|TrieQueryTokenizer
specifier|public
specifier|static
class|class
name|TrieQueryTokenizer
extends|extends
name|Tokenizer
block|{
DECL|field|dateField
specifier|private
specifier|static
specifier|final
name|DateField
name|dateField
init|=
operator|new
name|DateField
argument_list|()
decl_stmt|;
DECL|field|number
specifier|private
specifier|final
name|String
name|number
decl_stmt|;
DECL|field|used
specifier|private
name|boolean
name|used
init|=
literal|false
decl_stmt|;
DECL|field|type
specifier|private
name|TrieField
operator|.
name|TrieTypes
name|type
decl_stmt|;
DECL|method|TrieQueryTokenizer
specifier|public
name|TrieQueryTokenizer
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|TrieField
operator|.
name|TrieTypes
name|type
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|char
index|[]
name|buf
init|=
operator|new
name|char
index|[
literal|8
index|]
decl_stmt|;
name|int
name|len
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|reader
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
name|builder
operator|.
name|append
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|number
operator|=
name|builder
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|Token
name|next
parameter_list|(
name|Token
name|token
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|used
condition|)
return|return
literal|null
return|;
name|String
name|value
init|=
name|number
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|INTEGER
case|:
name|value
operator|=
name|TrieUtils
operator|.
name|intToPrefixCoded
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|number
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT
case|:
name|value
operator|=
name|TrieUtils
operator|.
name|intToPrefixCoded
argument_list|(
name|TrieUtils
operator|.
name|floatToSortableInt
argument_list|(
name|Float
operator|.
name|parseFloat
argument_list|(
name|number
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|LONG
case|:
name|value
operator|=
name|TrieUtils
operator|.
name|longToPrefixCoded
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|number
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
name|value
operator|=
name|TrieUtils
operator|.
name|longToPrefixCoded
argument_list|(
name|TrieUtils
operator|.
name|doubleToSortableLong
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|number
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|DATE
case|:
name|value
operator|=
name|TrieUtils
operator|.
name|longToPrefixCoded
argument_list|(
name|dateField
operator|.
name|parseMath
argument_list|(
literal|null
argument_list|,
name|number
argument_list|)
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Unknown type for trie field"
argument_list|)
throw|;
block|}
name|token
operator|.
name|reinit
argument_list|(
name|value
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|token
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|used
operator|=
literal|true
expr_stmt|;
return|return
name|token
return|;
block|}
block|}
block|}
end_class

end_unit


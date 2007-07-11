begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|search
operator|.
name|Filter
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
name|TermEnum
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
name|TermDocs
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
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

begin_comment
comment|/**  * @version $Id$  */
end_comment

begin_class
DECL|class|PrefixFilter
specifier|public
class|class
name|PrefixFilter
extends|extends
name|Filter
block|{
DECL|field|prefix
specifier|protected
specifier|final
name|Term
name|prefix
decl_stmt|;
DECL|method|PrefixFilter
name|PrefixFilter
parameter_list|(
name|Term
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
block|}
DECL|method|getPrefix
name|Term
name|getPrefix
parameter_list|()
block|{
return|return
name|prefix
return|;
block|}
DECL|method|bits
specifier|public
name|BitSet
name|bits
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|BitSet
name|bitSet
init|=
operator|new
name|BitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
operator|new
name|PrefixGenerator
argument_list|(
name|prefix
argument_list|)
block|{
specifier|public
name|void
name|handleDoc
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|bitSet
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
operator|.
name|generate
argument_list|(
name|reader
argument_list|)
expr_stmt|;
return|return
name|bitSet
return|;
block|}
block|}
end_class

begin_comment
comment|// keep this protected until I decide if it's a good way
end_comment

begin_comment
comment|// to separate id generation from collection (or should
end_comment

begin_comment
comment|// I just reuse hitcollector???)
end_comment

begin_interface
DECL|interface|IdGenerator
interface|interface
name|IdGenerator
block|{
DECL|method|generate
specifier|public
name|void
name|generate
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|handleDoc
specifier|public
name|void
name|handleDoc
parameter_list|(
name|int
name|doc
parameter_list|)
function_decl|;
block|}
end_interface

begin_class
DECL|class|PrefixGenerator
specifier|abstract
class|class
name|PrefixGenerator
implements|implements
name|IdGenerator
block|{
DECL|field|prefix
specifier|protected
specifier|final
name|Term
name|prefix
decl_stmt|;
DECL|method|PrefixGenerator
name|PrefixGenerator
parameter_list|(
name|Term
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
block|}
DECL|method|generate
specifier|public
name|void
name|generate
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|TermEnum
name|enumerator
init|=
name|reader
operator|.
name|terms
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
name|TermDocs
name|termDocs
init|=
name|reader
operator|.
name|termDocs
argument_list|()
decl_stmt|;
try|try
block|{
name|String
name|prefixText
init|=
name|prefix
operator|.
name|text
argument_list|()
decl_stmt|;
name|String
name|prefixField
init|=
name|prefix
operator|.
name|field
argument_list|()
decl_stmt|;
do|do
block|{
name|Term
name|term
init|=
name|enumerator
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|!=
literal|null
operator|&&
name|term
operator|.
name|text
argument_list|()
operator|.
name|startsWith
argument_list|(
name|prefixText
argument_list|)
operator|&&
name|term
operator|.
name|field
argument_list|()
operator|==
name|prefixField
condition|)
block|{
name|termDocs
operator|.
name|seek
argument_list|(
name|term
argument_list|)
expr_stmt|;
while|while
condition|(
name|termDocs
operator|.
name|next
argument_list|()
condition|)
block|{
name|handleDoc
argument_list|(
name|termDocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
break|break;
block|}
block|}
do|while
condition|(
name|enumerator
operator|.
name|next
argument_list|()
condition|)
do|;
block|}
finally|finally
block|{
name|termDocs
operator|.
name|close
argument_list|()
expr_stmt|;
name|enumerator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit


begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_comment
comment|/**  * Subclass of FilteredTermsEnum for enumerating a single term.  *<p>  * This can be used by {@link MultiTermQuery}s that need only visit one term,  * but want to preserve MultiTermQuery semantics such as  * {@link MultiTermQuery#rewriteMethod}.  */
end_comment

begin_class
DECL|class|SingleTermsEnum
specifier|public
specifier|final
class|class
name|SingleTermsEnum
extends|extends
name|FilteredTermsEnum
block|{
DECL|field|singleRef
specifier|private
specifier|final
name|BytesRef
name|singleRef
decl_stmt|;
comment|/**    * Creates a new<code>SingleTermsEnum</code>.    *<p>    * After calling the constructor the enumeration is already pointing to the term,    * if it exists.    */
DECL|method|SingleTermsEnum
specifier|public
name|SingleTermsEnum
parameter_list|(
name|TermsEnum
name|tenum
parameter_list|,
name|Term
name|singleTerm
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|tenum
argument_list|)
expr_stmt|;
name|singleRef
operator|=
name|singleTerm
operator|.
name|bytes
argument_list|()
expr_stmt|;
name|setInitialSeekTerm
argument_list|(
name|singleRef
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|accept
specifier|protected
name|AcceptStatus
name|accept
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
return|return
name|term
operator|.
name|equals
argument_list|(
name|singleRef
argument_list|)
condition|?
name|AcceptStatus
operator|.
name|YES
else|:
name|AcceptStatus
operator|.
name|END
return|;
block|}
block|}
end_class

end_unit


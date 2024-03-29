begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.tokenattributes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
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
name|util
operator|.
name|Attribute
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
comment|/**  * This attribute is requested by TermsHashPerField to index the contents.  * This attribute can be used to customize the final byte[] encoding of terms.  *<p>  * Consumers of this attribute call {@link #getBytesRef()} for each term. Example:  *<pre class="prettyprint">  *   final TermToBytesRefAttribute termAtt = tokenStream.getAttribute(TermToBytesRefAttribute.class);  *  *   while (tokenStream.incrementToken() {  *     final BytesRef bytes = termAtt.getBytesRef();  *  *     if (isInteresting(bytes)) {  *       *       // because the bytes are reused by the attribute (like CharTermAttribute's char[] buffer),  *       // you should make a copy if you need persistent access to the bytes, otherwise they will  *       // be rewritten across calls to incrementToken()  *  *       doSomethingWith(BytesRef.deepCopyOf(bytes));  *     }  *   }  *   ...  *</pre>  * @lucene.internal This is a very expert and internal API, please use  * {@link CharTermAttribute} and its implementation for UTF-8 terms; to  * index binary terms, use {@link BytesTermAttribute} and its implementation.  */
end_comment

begin_interface
DECL|interface|TermToBytesRefAttribute
specifier|public
interface|interface
name|TermToBytesRefAttribute
extends|extends
name|Attribute
block|{
comment|/**    * Retrieve this attribute's BytesRef. The bytes are updated from the current term.    * The implementation may return a new instance or keep the previous one.    * @return a BytesRef to be indexed (only stays valid until token stream gets incremented)    */
DECL|method|getBytesRef
specifier|public
name|BytesRef
name|getBytesRef
parameter_list|()
function_decl|;
block|}
end_interface

end_unit


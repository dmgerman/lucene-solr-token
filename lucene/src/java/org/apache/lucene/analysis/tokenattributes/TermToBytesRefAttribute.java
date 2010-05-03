begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
comment|/**  * This attribute is requested by TermsHashPerField to index the contents.  * This attribute has no real state, it should be implemented in addition to  * {@link CharTermAttribute}, to support indexing the term text as  * UTF-8 bytes.  * @lucene.experimental This is a very expert API, please use  * {@link CharTermAttributeImpl} and its implementation of this method  * for UTF-8 terms.  */
end_comment

begin_interface
DECL|interface|TermToBytesRefAttribute
specifier|public
interface|interface
name|TermToBytesRefAttribute
extends|extends
name|Attribute
block|{
comment|/** Copies the token's term text into the given {@link BytesRef}.    * @param termBytes destination to write the bytes to (UTF-8 for text terms).    * The length of the BytesRef's buffer may be not large enough, so you need to grow.    * The parameters' {@code bytes} is guaranteed to be not {@code null}.    * @return the hashcode as defined by {@link BytesRef#hashCode}:    *<pre>    *  int hash = 0;    *  for (int i = termBytes.offset; i&lt; termBytes.offset+termBytes.length; i++) {    *    hash = 31*hash + termBytes.bytes[i];    *  }    *</pre>    * Implement this for performance reasons, if your code can calculate    * the hash on-the-fly. If this is not the case, just return    * {@code termBytes.hashCode()}.    */
DECL|method|toBytesRef
specifier|public
name|int
name|toBytesRef
parameter_list|(
name|BytesRef
name|termBytes
parameter_list|)
function_decl|;
block|}
end_interface

end_unit


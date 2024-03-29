begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.phonetic
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|phonetic
package|;
end_package

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
name|util
operator|.
name|TokenFilterFactory
import|;
end_import

begin_comment
comment|/**  * Factory for {@link DaitchMokotoffSoundexFilter}.  *  * Create tokens based on DaitchâMokotoff Soundex phonetic filter.  *<p>  * This takes one optional argument:  *<dl>  *<dt>inject</dt><dd> (default=true) add tokens to the stream with the offset=0</dd>  *</dl>  *  *<pre class="prettyprint">  *&lt;fieldType name="text_phonetic" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;filter class="solr.DaitchMokotoffSoundexFilterFactory" inject="true"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *  * @see DaitchMokotoffSoundexFilter  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|DaitchMokotoffSoundexFilterFactory
specifier|public
class|class
name|DaitchMokotoffSoundexFilterFactory
extends|extends
name|TokenFilterFactory
block|{
comment|/** parameter name: true if encoded tokens should be added as synonyms */
DECL|field|INJECT
specifier|public
specifier|static
specifier|final
name|String
name|INJECT
init|=
literal|"inject"
decl_stmt|;
comment|// boolean
DECL|field|inject
specifier|final
name|boolean
name|inject
decl_stmt|;
comment|//accessed by the test
comment|/** Creates a new PhoneticFilterFactory */
DECL|method|DaitchMokotoffSoundexFilterFactory
specifier|public
name|DaitchMokotoffSoundexFilterFactory
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|inject
operator|=
name|getBoolean
argument_list|(
name|args
argument_list|,
name|INJECT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown parameters: "
operator|+
name|args
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|DaitchMokotoffSoundexFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|DaitchMokotoffSoundexFilter
argument_list|(
name|input
argument_list|,
name|inject
argument_list|)
return|;
block|}
block|}
end_class

end_unit


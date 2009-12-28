begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.ext
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|ext
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
name|analysis
operator|.
name|Analyzer
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
name|queryParser
operator|.
name|ParseException
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
name|queryParser
operator|.
name|QueryParser
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
name|queryParser
operator|.
name|ext
operator|.
name|Extensions
operator|.
name|Pair
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
name|util
operator|.
name|Version
import|;
end_import

begin_comment
comment|/**  * The {@link ExtendableQueryParser} enables arbitrary query parser extension  * based on a customizable field naming scheme. The lucene query syntax allows  * implicit and explicit field definitions as query prefix followed by a colon  * (':') character. The {@link ExtendableQueryParser} allows to encode extension  * keys into the field symbol associated with a registered instance of  * {@link ParserExtension}. A customizable separation character separates the  * extension key from the actual field symbol. The {@link ExtendableQueryParser}  * splits (@see {@link Extensions#splitExtensionField(String, String)}) the  * extension key from the field symbol and tries to resolve the associated  * {@link ParserExtension}. If the parser can't resolve the key or the field  * token does not contain a separation character, {@link ExtendableQueryParser}  * yields the same behavior as its super class {@link QueryParser}. Otherwise,  * if the key is associated with a {@link ParserExtension} instance, the parser  * builds an instance of {@link ExtensionQuery} to be processed by  * {@link ParserExtension#parse(ExtensionQuery)}.If a extension field does not  * contain a field part the default field for the query will be used.  *<p>  * To guarantee that an extension field is processed with its associated  * extension, the extension query part must escape any special characters like  * '*' or '['. If the extension query contains any whitespace characters, the  * extension query part must be enclosed in quotes.  * Example ('_' used as separation character):  *<pre>  *   title_customExt:"Apache Lucene\?" OR content_customExt:prefix\*  *</pre>  *   * Search on the default field:  *<pre>  *   _customExt:"Apache Lucene\?" OR _customExt:prefix\*  *</pre>  *</p>  *<p>  * The {@link ExtendableQueryParser} itself does not implement the logic how  * field and extension key are separated or ordered. All logic regarding the  * extension key and field symbol parsing is located in {@link Extensions}.  * Customized extension schemes should be implemented by sub-classing  * {@link Extensions}.  *</p>  *<p>  * For details about the default encoding scheme see {@link Extensions}.  *</p>  *   * @see Extensions  * @see ParserExtension  * @see ExtensionQuery  */
end_comment

begin_class
DECL|class|ExtendableQueryParser
specifier|public
class|class
name|ExtendableQueryParser
extends|extends
name|QueryParser
block|{
DECL|field|defaultField
specifier|private
specifier|final
name|String
name|defaultField
decl_stmt|;
DECL|field|extensions
specifier|private
specifier|final
name|Extensions
name|extensions
decl_stmt|;
comment|/**    * Default empty extensions instance    */
DECL|field|DEFAULT_EXTENSION
specifier|private
specifier|static
specifier|final
name|Extensions
name|DEFAULT_EXTENSION
init|=
operator|new
name|Extensions
argument_list|()
decl_stmt|;
comment|/**    * Creates a new {@link ExtendableQueryParser} instance    *     * @param matchVersion    *          the lucene version to use.    * @param f    *          the default query field    * @param a    *          the analyzer used to find terms in a query string    */
DECL|method|ExtendableQueryParser
specifier|public
name|ExtendableQueryParser
parameter_list|(
specifier|final
name|Version
name|matchVersion
parameter_list|,
specifier|final
name|String
name|f
parameter_list|,
specifier|final
name|Analyzer
name|a
parameter_list|)
block|{
name|this
argument_list|(
name|matchVersion
argument_list|,
name|f
argument_list|,
name|a
argument_list|,
name|DEFAULT_EXTENSION
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@link ExtendableQueryParser} instance    *     * @param matchVersion    *          the lucene version to use.    * @param f    *          the default query field    * @param a    *          the analyzer used to find terms in a query string    * @param ext    *          the query parser extensions    */
DECL|method|ExtendableQueryParser
specifier|public
name|ExtendableQueryParser
parameter_list|(
specifier|final
name|Version
name|matchVersion
parameter_list|,
specifier|final
name|String
name|f
parameter_list|,
specifier|final
name|Analyzer
name|a
parameter_list|,
specifier|final
name|Extensions
name|ext
parameter_list|)
block|{
name|super
argument_list|(
name|matchVersion
argument_list|,
name|f
argument_list|,
name|a
argument_list|)
expr_stmt|;
name|this
operator|.
name|defaultField
operator|=
name|f
expr_stmt|;
name|this
operator|.
name|extensions
operator|=
name|ext
expr_stmt|;
block|}
comment|/**    * Returns the extension field delimiter character.    *     * @return the extension field delimiter character.    */
DECL|method|getExtensionFieldDelimiter
specifier|public
name|char
name|getExtensionFieldDelimiter
parameter_list|()
block|{
return|return
name|extensions
operator|.
name|getExtensionFieldDelimiter
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldQuery
specifier|protected
name|Query
name|getFieldQuery
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|String
name|queryText
parameter_list|)
throws|throws
name|ParseException
block|{
specifier|final
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|splitExtensionField
init|=
name|this
operator|.
name|extensions
operator|.
name|splitExtensionField
argument_list|(
name|defaultField
argument_list|,
name|field
argument_list|)
decl_stmt|;
specifier|final
name|ParserExtension
name|extension
init|=
name|this
operator|.
name|extensions
operator|.
name|getExtension
argument_list|(
name|splitExtensionField
operator|.
name|cud
argument_list|)
decl_stmt|;
if|if
condition|(
name|extension
operator|!=
literal|null
condition|)
block|{
return|return
name|extension
operator|.
name|parse
argument_list|(
operator|new
name|ExtensionQuery
argument_list|(
name|this
argument_list|,
name|splitExtensionField
operator|.
name|cur
argument_list|,
name|queryText
argument_list|)
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|getFieldQuery
argument_list|(
name|field
argument_list|,
name|queryText
argument_list|)
return|;
block|}
block|}
end_class

end_unit


begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
import|;
end_import

begin_comment
comment|/**  *<p>  * An {@link EntityProcessor} instance which can stream lines of text read from a   * datasource. Options allow lines to be explicitly skipped or included in the index.  *</p>  *<p/>  *<p>  * Attribute summary   *<ul>  *<li>url is the required location of the input file. If this value is  *     relative, it assumed to be relative to baseLoc.</li>  *<li>acceptLineRegex is an optional attribute that if present discards any   *     line which does not match the regExp.</li>  *<li>skipLineRegex is an optional attribute that is applied after any   *     acceptLineRegex and discards any line which matches this regExp.</li>  *</ul>  *</p><p>  * Although envisioned for reading lines from a file or url, LineEntityProcessor may also be useful  * for dealing with change lists, where each line contains filenames which can be used by subsequent entities  * to parse content from those files.  *<p/>  *<p>  * Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>  * for more details.  *</p>  *<p/>  *<b>This API is experimental and may change in the future.</b>  *  * @since solr 1.4  * @see Pattern  */
end_comment

begin_class
DECL|class|LineEntityProcessor
specifier|public
class|class
name|LineEntityProcessor
extends|extends
name|EntityProcessorBase
block|{
DECL|field|acceptLineRegex
DECL|field|skipLineRegex
specifier|private
name|Pattern
name|acceptLineRegex
decl_stmt|,
name|skipLineRegex
decl_stmt|;
DECL|field|url
specifier|private
name|String
name|url
decl_stmt|;
DECL|field|reader
specifier|private
name|BufferedReader
name|reader
decl_stmt|;
comment|/**    * Parses each of the entity attributes.    */
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|String
name|s
decl_stmt|;
comment|// init a regex to locate files from the input we want to index
name|s
operator|=
name|context
operator|.
name|getResolvedEntityAttribute
argument_list|(
name|ACCEPT_LINE_REGEX
argument_list|)
expr_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
name|acceptLineRegex
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
comment|// init a regex to locate files from the input to be skipped
name|s
operator|=
name|context
operator|.
name|getResolvedEntityAttribute
argument_list|(
name|SKIP_LINE_REGEX
argument_list|)
expr_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
name|skipLineRegex
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
comment|// the FileName is required.
name|url
operator|=
name|context
operator|.
name|getResolvedEntityAttribute
argument_list|(
name|URL
argument_list|)
expr_stmt|;
if|if
condition|(
name|url
operator|==
literal|null
condition|)
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"'"
operator|+
name|URL
operator|+
literal|"' is a required attribute"
argument_list|)
throw|;
block|}
comment|/**    * Reads lines from the url till it finds a lines that matches the    * optional acceptLineRegex and does not match the optional skipLineRegex.    *    * @return A row containing a minimum of one field "rawLine" or null to signal    * end of file. The rawLine is the as line as returned by readLine()    * from the url. However transformers can be used to create as     * many other fields as required.    */
annotation|@
name|Override
DECL|method|nextRow
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|nextRow
parameter_list|()
block|{
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|(
name|Reader
operator|)
name|context
operator|.
name|getDataSource
argument_list|()
operator|.
name|getData
argument_list|(
name|url
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|line
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|// read a line from the input file
try|try
block|{
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|exp
parameter_list|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|DataImportHandlerException
operator|.
name|SEVERE
argument_list|,
literal|"Problem reading from input"
argument_list|,
name|exp
argument_list|)
throw|;
block|}
comment|// end of input
if|if
condition|(
name|line
operator|==
literal|null
condition|)
block|{
name|closeResources
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|// First scan whole line to see if we want it
if|if
condition|(
name|acceptLineRegex
operator|!=
literal|null
operator|&&
operator|!
name|acceptLineRegex
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
operator|.
name|find
argument_list|()
condition|)
continue|continue;
if|if
condition|(
name|skipLineRegex
operator|!=
literal|null
operator|&&
name|skipLineRegex
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
operator|.
name|find
argument_list|()
condition|)
continue|continue;
comment|// Contruct the 'row' of fields
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|row
operator|.
name|put
argument_list|(
literal|"rawLine"
argument_list|,
name|line
argument_list|)
expr_stmt|;
return|return
name|row
return|;
block|}
block|}
DECL|method|closeResources
specifier|public
name|void
name|closeResources
parameter_list|()
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
name|reader
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|destroy
specifier|public
name|void
name|destroy
parameter_list|()
block|{
name|closeResources
argument_list|()
expr_stmt|;
name|super
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
comment|/**    * Holds the name of entity attribute that will be parsed to obtain    * the filename containing the changelist.    */
DECL|field|URL
specifier|public
specifier|static
specifier|final
name|String
name|URL
init|=
literal|"url"
decl_stmt|;
comment|/**    * Holds the name of entity attribute that will be parsed to obtain    * the pattern to be used when checking to see if a line should    * be returned.    */
DECL|field|ACCEPT_LINE_REGEX
specifier|public
specifier|static
specifier|final
name|String
name|ACCEPT_LINE_REGEX
init|=
literal|"acceptLineRegex"
decl_stmt|;
comment|/**    * Holds the name of entity attribute that will be parsed to obtain    * the pattern to be used when checking to see if a line should    * be ignored.    */
DECL|field|SKIP_LINE_REGEX
specifier|public
specifier|static
specifier|final
name|String
name|SKIP_LINE_REGEX
init|=
literal|"skipLineRegex"
decl_stmt|;
block|}
end_class

end_unit


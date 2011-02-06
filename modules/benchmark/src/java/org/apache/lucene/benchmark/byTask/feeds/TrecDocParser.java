begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
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
name|File
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
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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

begin_comment
comment|/**   * Parser for trec doc content, invoked on doc text excluding<DOC> and<DOCNO>  * which are handled in TrecContentSource. Required to be stateless and hence thread safe.   */
end_comment

begin_class
DECL|class|TrecDocParser
specifier|public
specifier|abstract
class|class
name|TrecDocParser
block|{
comment|/** Types of trec parse paths, */
DECL|enum|ParsePathType
DECL|enum constant|GOV2
DECL|enum constant|FBIS
DECL|enum constant|FT
DECL|enum constant|FR94
DECL|enum constant|LATIMES
specifier|public
enum|enum
name|ParsePathType
block|{
name|GOV2
block|,
name|FBIS
block|,
name|FT
block|,
name|FR94
block|,
name|LATIMES
block|}
comment|/** trec parser type used for unknown extensions */
DECL|field|DEFAULT_PATH_TYPE
specifier|public
specifier|static
specifier|final
name|ParsePathType
name|DEFAULT_PATH_TYPE
init|=
name|ParsePathType
operator|.
name|GOV2
decl_stmt|;
DECL|field|pathType2parser
specifier|static
specifier|final
name|Map
argument_list|<
name|ParsePathType
argument_list|,
name|TrecDocParser
argument_list|>
name|pathType2parser
init|=
operator|new
name|HashMap
argument_list|<
name|ParsePathType
argument_list|,
name|TrecDocParser
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|pathType2parser
operator|.
name|put
argument_list|(
name|ParsePathType
operator|.
name|GOV2
argument_list|,
operator|new
name|TrecGov2Parser
argument_list|()
argument_list|)
expr_stmt|;
name|pathType2parser
operator|.
name|put
argument_list|(
name|ParsePathType
operator|.
name|FBIS
argument_list|,
operator|new
name|TrecFBISParser
argument_list|()
argument_list|)
expr_stmt|;
name|pathType2parser
operator|.
name|put
argument_list|(
name|ParsePathType
operator|.
name|FR94
argument_list|,
operator|new
name|TrecFR94Parser
argument_list|()
argument_list|)
expr_stmt|;
name|pathType2parser
operator|.
name|put
argument_list|(
name|ParsePathType
operator|.
name|FT
argument_list|,
operator|new
name|TrecFTParser
argument_list|()
argument_list|)
expr_stmt|;
name|pathType2parser
operator|.
name|put
argument_list|(
name|ParsePathType
operator|.
name|LATIMES
argument_list|,
operator|new
name|TrecLATimesParser
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|field|pathName2Type
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ParsePathType
argument_list|>
name|pathName2Type
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ParsePathType
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
for|for
control|(
name|ParsePathType
name|ppt
range|:
name|ParsePathType
operator|.
name|values
argument_list|()
control|)
block|{
name|pathName2Type
operator|.
name|put
argument_list|(
name|ppt
operator|.
name|name
argument_list|()
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|,
name|ppt
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** max length of walk up from file to its ancestors when looking for a known path type */
DECL|field|MAX_PATH_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|MAX_PATH_LENGTH
init|=
literal|10
decl_stmt|;
comment|/**    * Compute the path type of a file by inspecting name of file and its parents    */
DECL|method|pathType
specifier|public
specifier|static
name|ParsePathType
name|pathType
parameter_list|(
name|File
name|f
parameter_list|)
block|{
name|int
name|pathLength
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|f
operator|!=
literal|null
operator|&&
operator|++
name|pathLength
operator|<
name|MAX_PATH_LENGTH
condition|)
block|{
name|ParsePathType
name|ppt
init|=
name|pathName2Type
operator|.
name|get
argument_list|(
name|f
operator|.
name|getName
argument_list|()
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|ppt
operator|!=
literal|null
condition|)
block|{
return|return
name|ppt
return|;
block|}
name|f
operator|=
name|f
operator|.
name|getParentFile
argument_list|()
expr_stmt|;
block|}
return|return
name|DEFAULT_PATH_TYPE
return|;
block|}
comment|/**     * parse the text prepared in docBuf into a result DocData,     * no synchronization is required.    * @param docData reusable result    * @param name name that should be set to the result    * @param trecSrc calling trec content source      * @param docBuf text to parse      * @param pathType type of parsed file, or null if unknown - may be used by     * parsers to alter their behavior according to the file path type.     */
DECL|method|parse
specifier|public
specifier|abstract
name|DocData
name|parse
parameter_list|(
name|DocData
name|docData
parameter_list|,
name|String
name|name
parameter_list|,
name|TrecContentSource
name|trecSrc
parameter_list|,
name|StringBuilder
name|docBuf
parameter_list|,
name|ParsePathType
name|pathType
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
comment|/**     * strip tags from<code>buf</code>: each tag is replaced by a single blank.    * @return text obtained when stripping all tags from<code>buf</code> (Input StringBuilder is unmodified).    */
DECL|method|stripTags
specifier|public
specifier|static
name|String
name|stripTags
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|int
name|start
parameter_list|)
block|{
return|return
name|stripTags
argument_list|(
name|buf
operator|.
name|substring
argument_list|(
name|start
argument_list|)
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**     * strip tags from input.    * @see #stripTags(StringBuilder, int)    */
DECL|method|stripTags
specifier|public
specifier|static
name|String
name|stripTags
parameter_list|(
name|String
name|buf
parameter_list|,
name|int
name|start
parameter_list|)
block|{
if|if
condition|(
name|start
operator|>
literal|0
condition|)
block|{
name|buf
operator|=
name|buf
operator|.
name|substring
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|replaceAll
argument_list|(
literal|"<[^>]*>"
argument_list|,
literal|" "
argument_list|)
return|;
block|}
comment|/**    * Extract from<code>buf</code> the text of interest within specified tags    * @param buf entire input text    * @param startTag tag marking start of text of interest     * @param endTag tag marking end of text of interest    * @param maxPos if&ge; 0 sets a limit on start of text of interest    * @return text of interest or null if not found    */
DECL|method|extract
specifier|public
specifier|static
name|String
name|extract
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|String
name|startTag
parameter_list|,
name|String
name|endTag
parameter_list|,
name|int
name|maxPos
parameter_list|,
name|String
name|noisePrefixes
index|[]
parameter_list|)
block|{
name|int
name|k1
init|=
name|buf
operator|.
name|indexOf
argument_list|(
name|startTag
argument_list|)
decl_stmt|;
if|if
condition|(
name|k1
operator|>=
literal|0
operator|&&
operator|(
name|maxPos
operator|<
literal|0
operator|||
name|k1
operator|<
name|maxPos
operator|)
condition|)
block|{
name|k1
operator|+=
name|startTag
operator|.
name|length
argument_list|()
expr_stmt|;
name|int
name|k2
init|=
name|buf
operator|.
name|indexOf
argument_list|(
name|endTag
argument_list|,
name|k1
argument_list|)
decl_stmt|;
if|if
condition|(
name|k2
operator|>=
literal|0
operator|&&
operator|(
name|maxPos
operator|<
literal|0
operator|||
name|k2
operator|<
name|maxPos
operator|)
condition|)
block|{
comment|// found end tag with allowed range
if|if
condition|(
name|noisePrefixes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|noise
range|:
name|noisePrefixes
control|)
block|{
name|int
name|k1a
init|=
name|buf
operator|.
name|indexOf
argument_list|(
name|noise
argument_list|,
name|k1
argument_list|)
decl_stmt|;
if|if
condition|(
name|k1a
operator|>=
literal|0
operator|&&
name|k1a
operator|<
name|k2
condition|)
block|{
name|k1
operator|=
name|k1a
operator|+
name|noise
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|buf
operator|.
name|substring
argument_list|(
name|k1
argument_list|,
name|k2
argument_list|)
operator|.
name|trim
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|//public static void main(String[] args) {
comment|//  System.out.println(stripTags("is it true that<space>2<<second space>><almost last space>1<one more space>?",0));
comment|//}
block|}
end_class

end_unit


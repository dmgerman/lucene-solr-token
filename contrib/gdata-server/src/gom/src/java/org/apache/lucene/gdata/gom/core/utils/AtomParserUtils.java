begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.gom.core.utils
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|gom
operator|.
name|core
operator|.
name|utils
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|lucene
operator|.
name|gdata
operator|.
name|gom
operator|.
name|AtomMediaType
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
name|gdata
operator|.
name|gom
operator|.
name|GOMLink
import|;
end_import

begin_comment
comment|/**  * @author Simon Willnauer  *   */
end_comment

begin_class
DECL|class|AtomParserUtils
specifier|public
class|class
name|AtomParserUtils
block|{
DECL|field|ATOM_MEDIA_TYPE_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|ATOM_MEDIA_TYPE_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|".+/.+"
argument_list|)
decl_stmt|;
comment|/** 	 * Replaces all xml character with the corresponding entity. 	 *  	 *<ul> 	 *<li>&lt;!ENTITY lt&quot;&amp;#38;#60;&quot;&gt;</li> 	 *<li>&lt;!ENTITY gt&quot;&amp;#62;&quot;&gt;</li> 	 *<li>&lt;!ENTITY amp&quot;&amp;#38;#38;&quot;&gt;</li> 	 *<li>&lt;!ENTITY apos&quot;&amp;#39;&quot;&gt;</li> 	 *<li>&lt;!ENTITY quot&quot;&amp;#34;&quot;&gt;</li> 	 *</ul> 	 *  	 * see<a 	 * href="http://www.w3.org/TR/2006/REC-xml-20060816/#intern-replacement">W3C 	 * specification</a> 	 *  	 * @param aString - 	 *            a string may container xml characters like '<' 	 * @return the input string with escaped xml characters 	 */
DECL|method|escapeXMLCharacter
specifier|public
specifier|static
name|String
name|escapeXMLCharacter
parameter_list|(
name|String
name|aString
parameter_list|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|char
index|[]
name|cs
init|=
name|aString
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|cs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
switch|switch
condition|(
name|cs
index|[
name|i
index|]
condition|)
block|{
case|case
literal|'<'
case|:
name|builder
operator|.
name|append
argument_list|(
literal|"&lt;"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'>'
case|:
name|builder
operator|.
name|append
argument_list|(
literal|"&gt;"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'"'
case|:
name|builder
operator|.
name|append
argument_list|(
literal|"&quot;"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\''
case|:
name|builder
operator|.
name|append
argument_list|(
literal|"&apos;"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'&'
case|:
name|builder
operator|.
name|append
argument_list|(
literal|"&amp;"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\0'
case|:
comment|// this breaks some xml serializer like soap serializer -->
comment|// remove it
break|break;
default|default:
name|builder
operator|.
name|append
argument_list|(
name|cs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|isAtomMediaType
specifier|public
specifier|static
name|boolean
name|isAtomMediaType
parameter_list|(
name|String
name|aMediaType
parameter_list|)
block|{
return|return
operator|(
name|aMediaType
operator|==
literal|null
operator|||
name|aMediaType
operator|.
name|length
argument_list|()
operator|<
literal|3
operator|)
condition|?
literal|false
else|:
name|ATOM_MEDIA_TYPE_PATTERN
operator|.
name|matcher
argument_list|(
name|aMediaType
argument_list|)
operator|.
name|matches
argument_list|()
return|;
block|}
DECL|method|getAtomMediaType
specifier|public
specifier|static
name|AtomMediaType
name|getAtomMediaType
parameter_list|(
name|String
name|aMediaType
parameter_list|)
block|{
if|if
condition|(
name|aMediaType
operator|==
literal|null
operator|||
operator|!
name|isAtomMediaType
argument_list|(
name|aMediaType
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"aMediaType must be a media type and  not be null "
argument_list|)
throw|;
if|if
condition|(
name|aMediaType
operator|.
name|endsWith
argument_list|(
literal|"+xml"
argument_list|)
operator|||
name|aMediaType
operator|.
name|endsWith
argument_list|(
literal|"/xml"
argument_list|)
condition|)
return|return
name|AtomMediaType
operator|.
name|XML
return|;
if|if
condition|(
name|aMediaType
operator|.
name|startsWith
argument_list|(
literal|"text/"
argument_list|)
condition|)
return|return
name|AtomMediaType
operator|.
name|TEXT
return|;
return|return
name|AtomMediaType
operator|.
name|BINARY
return|;
block|}
DECL|method|getAbsolutAtomURI
specifier|public
specifier|static
name|String
name|getAbsolutAtomURI
parameter_list|(
name|String
name|xmlBase
parameter_list|,
name|String
name|atomUri
parameter_list|)
throws|throws
name|URISyntaxException
block|{
if|if
condition|(
name|atomUri
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"atomUri must not be null"
argument_list|)
throw|;
if|if
condition|(
name|atomUri
operator|.
name|startsWith
argument_list|(
literal|"www."
argument_list|)
condition|)
name|atomUri
operator|=
literal|"http://"
operator|+
name|atomUri
expr_stmt|;
name|URI
name|aUri
init|=
operator|new
name|URI
argument_list|(
name|atomUri
argument_list|)
decl_stmt|;
if|if
condition|(
name|xmlBase
operator|==
literal|null
operator|||
name|xmlBase
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
if|if
condition|(
operator|!
name|aUri
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|URISyntaxException
argument_list|(
name|atomUri
argument_list|,
literal|" -- no xml:base specified atom uri must be an absolute url"
argument_list|)
throw|;
block|}
block|}
return|return
name|atomUri
return|;
block|}
comment|/** 	 * Compares two links with rel attribute "alternate" Checks if href and type 	 * are equal 	 *  	 * @param left - 	 *            left link to compare 	 * @param right - 	 *            right link to compare 	 * @return<code>true</code> if and only if href and type are equal, 	 *         otherwise<code>false</code> 	 */
DECL|method|compareAlternateLinks
specifier|public
specifier|static
name|boolean
name|compareAlternateLinks
parameter_list|(
name|GOMLink
name|left
parameter_list|,
name|GOMLink
name|right
parameter_list|)
block|{
if|if
condition|(
operator|(
name|left
operator|.
name|getType
argument_list|()
operator|==
literal|null
operator|)
operator|^
name|right
operator|.
name|getType
argument_list|()
operator|==
literal|null
operator|||
operator|(
name|left
operator|.
name|getType
argument_list|()
operator|==
literal|null
operator|&&
name|right
operator|.
name|getType
argument_list|()
operator|==
literal|null
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|left
operator|.
name|getType
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|right
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|(
operator|(
name|left
operator|.
name|getHrefLang
argument_list|()
operator|==
literal|null
operator|)
operator|^
name|right
operator|.
name|getHrefLang
argument_list|()
operator|==
literal|null
operator|)
operator|||
operator|(
name|left
operator|.
name|getHrefLang
argument_list|()
operator|==
literal|null
operator|&&
name|right
operator|.
name|getHrefLang
argument_list|()
operator|==
literal|null
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|left
operator|.
name|getHrefLang
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|right
operator|.
name|getHrefLang
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
comment|// String s = new String(
comment|// "<!ENTITY lt \"&#38;#60;\"><!ENTITY gt \"&#62;\"><!ENTITY amp
comment|// \"&#38;#38;\"><!ENTITY apos \"&#39;\"><!ENTITY quot \"&#34;\">");
comment|// System.out.println(escapeXMLCharacter(s));
comment|//
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|isAtomMediaType
argument_list|(
literal|"t/h"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


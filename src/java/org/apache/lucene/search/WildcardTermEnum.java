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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Term
import|;
end_import

begin_comment
comment|/**  * Subclass of FilteredTermEnum for enumerating all terms that match the  * specified wildcard filter term.  *<p>  * Term enumerations are always ordered by Term.compareTo().  Each term in  * the enumeration is greater than all that precede it.  *  * @version $Id$  */
end_comment

begin_class
DECL|class|WildcardTermEnum
specifier|public
class|class
name|WildcardTermEnum
extends|extends
name|FilteredTermEnum
block|{
DECL|field|searchTerm
name|Term
name|searchTerm
decl_stmt|;
DECL|field|field
name|String
name|field
init|=
literal|""
decl_stmt|;
DECL|field|text
name|String
name|text
init|=
literal|""
decl_stmt|;
DECL|field|pre
name|String
name|pre
init|=
literal|""
decl_stmt|;
DECL|field|preLen
name|int
name|preLen
init|=
literal|0
decl_stmt|;
DECL|field|endEnum
name|boolean
name|endEnum
init|=
literal|false
decl_stmt|;
comment|/**    * Creates a new<code>WildcardTermEnum</code>.  Passing in a    * {@link org.apache.lucene.index.Term Term} that does not contain a    *<code>WILDCARD_CHAR</code> will cause an exception to be thrown.    *<p>    * After calling the constructor the enumeration is already pointing to the first     * valid term if such a term exists.    */
DECL|method|WildcardTermEnum
specifier|public
name|WildcardTermEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
name|searchTerm
operator|=
name|term
expr_stmt|;
name|field
operator|=
name|searchTerm
operator|.
name|field
argument_list|()
expr_stmt|;
name|text
operator|=
name|searchTerm
operator|.
name|text
argument_list|()
expr_stmt|;
name|int
name|sidx
init|=
name|text
operator|.
name|indexOf
argument_list|(
name|WILDCARD_STRING
argument_list|)
decl_stmt|;
name|int
name|cidx
init|=
name|text
operator|.
name|indexOf
argument_list|(
name|WILDCARD_CHAR
argument_list|)
decl_stmt|;
name|int
name|idx
init|=
name|sidx
decl_stmt|;
if|if
condition|(
name|idx
operator|==
operator|-
literal|1
condition|)
block|{
name|idx
operator|=
name|cidx
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cidx
operator|>=
literal|0
condition|)
block|{
name|idx
operator|=
name|Math
operator|.
name|min
argument_list|(
name|idx
argument_list|,
name|cidx
argument_list|)
expr_stmt|;
block|}
name|pre
operator|=
name|searchTerm
operator|.
name|text
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
expr_stmt|;
name|preLen
operator|=
name|pre
operator|.
name|length
argument_list|()
expr_stmt|;
name|text
operator|=
name|text
operator|.
name|substring
argument_list|(
name|preLen
argument_list|)
expr_stmt|;
name|setEnum
argument_list|(
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|searchTerm
operator|.
name|field
argument_list|()
argument_list|,
name|pre
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|termCompare
specifier|protected
specifier|final
name|boolean
name|termCompare
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
if|if
condition|(
name|field
operator|==
name|term
operator|.
name|field
argument_list|()
condition|)
block|{
name|String
name|searchText
init|=
name|term
operator|.
name|text
argument_list|()
decl_stmt|;
if|if
condition|(
name|searchText
operator|.
name|startsWith
argument_list|(
name|pre
argument_list|)
condition|)
block|{
return|return
name|wildcardEquals
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|searchText
argument_list|,
name|preLen
argument_list|)
return|;
block|}
block|}
name|endEnum
operator|=
literal|true
expr_stmt|;
return|return
literal|false
return|;
block|}
DECL|method|difference
specifier|public
specifier|final
name|float
name|difference
parameter_list|()
block|{
return|return
literal|1.0f
return|;
block|}
DECL|method|endEnum
specifier|public
specifier|final
name|boolean
name|endEnum
parameter_list|()
block|{
return|return
name|endEnum
return|;
block|}
comment|/********************************************    * String equality with support for wildcards    ********************************************/
DECL|field|WILDCARD_STRING
specifier|public
specifier|static
specifier|final
name|char
name|WILDCARD_STRING
init|=
literal|'*'
decl_stmt|;
DECL|field|WILDCARD_CHAR
specifier|public
specifier|static
specifier|final
name|char
name|WILDCARD_CHAR
init|=
literal|'?'
decl_stmt|;
comment|/**    * Determines if a word matches a wildcard pattern.    *<small>Work released by Granta Design Ltd after originally being done on    * company time.</small>    */
DECL|method|wildcardEquals
specifier|public
specifier|static
specifier|final
name|boolean
name|wildcardEquals
parameter_list|(
name|String
name|pattern
parameter_list|,
name|int
name|patternIdx
parameter_list|,
name|String
name|string
parameter_list|,
name|int
name|stringIdx
parameter_list|)
block|{
name|int
name|p
init|=
name|patternIdx
decl_stmt|;
for|for
control|(
name|int
name|s
init|=
name|stringIdx
init|;
condition|;
operator|++
name|p
operator|,
operator|++
name|s
control|)
block|{
comment|// End of string yet?
name|boolean
name|sEnd
init|=
operator|(
name|s
operator|>=
name|string
operator|.
name|length
argument_list|()
operator|)
decl_stmt|;
comment|// End of pattern yet?
name|boolean
name|pEnd
init|=
operator|(
name|p
operator|>=
name|pattern
operator|.
name|length
argument_list|()
operator|)
decl_stmt|;
comment|// If we're looking at the end of the string...
if|if
condition|(
name|sEnd
condition|)
block|{
comment|// Assume the only thing left on the pattern is/are wildcards
name|boolean
name|justWildcardsLeft
init|=
literal|true
decl_stmt|;
comment|// Current wildcard position
name|int
name|wildcardSearchPos
init|=
name|p
decl_stmt|;
comment|// While we haven't found the end of the pattern,
comment|// and haven't encountered any non-wildcard characters
while|while
condition|(
name|wildcardSearchPos
operator|<
name|pattern
operator|.
name|length
argument_list|()
operator|&&
name|justWildcardsLeft
condition|)
block|{
comment|// Check the character at the current position
name|char
name|wildchar
init|=
name|pattern
operator|.
name|charAt
argument_list|(
name|wildcardSearchPos
argument_list|)
decl_stmt|;
comment|// If it's not a wildcard character, then there is more
comment|// pattern information after this/these wildcards.
if|if
condition|(
name|wildchar
operator|!=
name|WILDCARD_CHAR
operator|&&
name|wildchar
operator|!=
name|WILDCARD_STRING
condition|)
block|{
name|justWildcardsLeft
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
comment|// Look at the next character
name|wildcardSearchPos
operator|++
expr_stmt|;
block|}
block|}
comment|// This was a prefix wildcard search, and we've matched, so
comment|// return true.
if|if
condition|(
name|justWildcardsLeft
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
comment|// If we've gone past the end of the string, or the pattern,
comment|// return false.
if|if
condition|(
name|sEnd
operator|||
name|pEnd
condition|)
block|{
break|break;
block|}
comment|// Match a single character, so continue.
if|if
condition|(
name|pattern
operator|.
name|charAt
argument_list|(
name|p
argument_list|)
operator|==
name|WILDCARD_CHAR
condition|)
block|{
continue|continue;
block|}
comment|//
if|if
condition|(
name|pattern
operator|.
name|charAt
argument_list|(
name|p
argument_list|)
operator|==
name|WILDCARD_STRING
condition|)
block|{
comment|// Look at the character beyond the '*'.
operator|++
name|p
expr_stmt|;
comment|// Examine the string, starting at the last character.
for|for
control|(
name|int
name|i
init|=
name|string
operator|.
name|length
argument_list|()
init|;
name|i
operator|>=
name|s
condition|;
operator|--
name|i
control|)
block|{
if|if
condition|(
name|wildcardEquals
argument_list|(
name|pattern
argument_list|,
name|p
argument_list|,
name|string
argument_list|,
name|i
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
break|break;
block|}
if|if
condition|(
name|pattern
operator|.
name|charAt
argument_list|(
name|p
argument_list|)
operator|!=
name|string
operator|.
name|charAt
argument_list|(
name|s
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|searchTerm
operator|=
literal|null
expr_stmt|;
name|field
operator|=
literal|null
expr_stmt|;
name|text
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit


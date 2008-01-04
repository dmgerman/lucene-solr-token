begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.wikipedia.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|wikipedia
operator|.
name|analysis
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|Token
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
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
name|Map
import|;
end_import

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|WikipediaTokenizerTest
specifier|public
class|class
name|WikipediaTokenizerTest
extends|extends
name|TestCase
block|{
DECL|method|WikipediaTokenizerTest
specifier|public
name|WikipediaTokenizerTest
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
block|{   }
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
block|{    }
DECL|method|testHandwritten
specifier|public
name|void
name|testHandwritten
parameter_list|()
throws|throws
name|Exception
block|{
comment|//make sure all tokens are in only one type
name|String
name|test
init|=
literal|"[[link]] This is a [[Category:foo]] Category  This is a linked [[:Category:bar none withstanding]] "
operator|+
literal|"Category This is (parens) This is a [[link]]  This is an external URL [http://lucene.apache.org] "
operator|+
literal|"Here is ''italics'' and ''more italics'', '''bold''' and '''''five quotes''''' "
operator|+
literal|" This is a [[link|display info]]  This is a period.  Here is $3.25 and here is 3.50.  Here's Johnny.  "
operator|+
literal|"==heading== ===sub head=== followed by some text  [[Category:blah| ]] "
operator|+
literal|"''[[Category:ital_cat]]''  here is some that is ''italics [[Category:foo]] but is never closed."
operator|+
literal|"'''same [[Category:foo]] goes for this '''''and2 [[Category:foo]] and this"
operator|+
literal|" [http://foo.boo.com/test/test/ Test Test] [http://foo.boo.com/test/test/test.html Test Test]"
operator|+
literal|" [http://foo.boo.com/test/test/test.html?g=b&c=d Test Test]<ref>Citation</ref><sup>martian</sup><span class=\"glue\">code</span>"
decl_stmt|;
name|Map
name|tcm
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
comment|//map tokens to types
name|tcm
operator|.
name|put
argument_list|(
literal|"link"
argument_list|,
name|WikipediaTokenizer
operator|.
name|INTERNAL_LINK
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"display"
argument_list|,
name|WikipediaTokenizer
operator|.
name|INTERNAL_LINK
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"info"
argument_list|,
name|WikipediaTokenizer
operator|.
name|INTERNAL_LINK
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"http://lucene.apache.org"
argument_list|,
name|WikipediaTokenizer
operator|.
name|EXTERNAL_LINK_URL
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"http://foo.boo.com/test/test/"
argument_list|,
name|WikipediaTokenizer
operator|.
name|EXTERNAL_LINK_URL
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"http://foo.boo.com/test/test/test.html"
argument_list|,
name|WikipediaTokenizer
operator|.
name|EXTERNAL_LINK_URL
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"http://foo.boo.com/test/test/test.html?g=b&c=d"
argument_list|,
name|WikipediaTokenizer
operator|.
name|EXTERNAL_LINK_URL
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"Test"
argument_list|,
name|WikipediaTokenizer
operator|.
name|EXTERNAL_LINK
argument_list|)
expr_stmt|;
comment|//alphanums
name|tcm
operator|.
name|put
argument_list|(
literal|"This"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"is"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"Category"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"linked"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"parens"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"external"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"URL"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"and"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"period"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"Here"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"Here's"
argument_list|,
literal|"<APOSTROPHE>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"here"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"Johnny"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"followed"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"by"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"text"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"that"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"but"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"never"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"closed"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"goes"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"for"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"this"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"an"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"some"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"martian"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"code"
argument_list|,
literal|"<ALPHANUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
name|WikipediaTokenizer
operator|.
name|CATEGORY
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"bar"
argument_list|,
name|WikipediaTokenizer
operator|.
name|CATEGORY
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"none"
argument_list|,
name|WikipediaTokenizer
operator|.
name|CATEGORY
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"withstanding"
argument_list|,
name|WikipediaTokenizer
operator|.
name|CATEGORY
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"blah"
argument_list|,
name|WikipediaTokenizer
operator|.
name|CATEGORY
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"ital"
argument_list|,
name|WikipediaTokenizer
operator|.
name|CATEGORY
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"cat"
argument_list|,
name|WikipediaTokenizer
operator|.
name|CATEGORY
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"italics"
argument_list|,
name|WikipediaTokenizer
operator|.
name|ITALICS
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"more"
argument_list|,
name|WikipediaTokenizer
operator|.
name|ITALICS
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"bold"
argument_list|,
name|WikipediaTokenizer
operator|.
name|BOLD
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"same"
argument_list|,
name|WikipediaTokenizer
operator|.
name|BOLD
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"five"
argument_list|,
name|WikipediaTokenizer
operator|.
name|BOLD_ITALICS
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"and2"
argument_list|,
name|WikipediaTokenizer
operator|.
name|BOLD_ITALICS
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"quotes"
argument_list|,
name|WikipediaTokenizer
operator|.
name|BOLD_ITALICS
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"heading"
argument_list|,
name|WikipediaTokenizer
operator|.
name|HEADING
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"sub"
argument_list|,
name|WikipediaTokenizer
operator|.
name|SUB_HEADING
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"head"
argument_list|,
name|WikipediaTokenizer
operator|.
name|SUB_HEADING
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"Citation"
argument_list|,
name|WikipediaTokenizer
operator|.
name|CITATION
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"3.25"
argument_list|,
literal|"<NUM>"
argument_list|)
expr_stmt|;
name|tcm
operator|.
name|put
argument_list|(
literal|"3.50"
argument_list|,
literal|"<NUM>"
argument_list|)
expr_stmt|;
name|WikipediaTokenizer
name|tf
init|=
operator|new
name|WikipediaTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|test
argument_list|)
argument_list|)
decl_stmt|;
name|Token
name|token
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|int
name|numItalics
init|=
literal|0
decl_stmt|;
name|int
name|numBoldItalics
init|=
literal|0
decl_stmt|;
name|int
name|numCategory
init|=
literal|0
decl_stmt|;
name|int
name|numCitation
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|tf
operator|.
name|next
argument_list|(
name|token
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
name|tokText
init|=
name|token
operator|.
name|termText
argument_list|()
decl_stmt|;
comment|//System.out.println("Text: " + tokText + " Type: " + token.type());
name|assertTrue
argument_list|(
literal|"token is null and it shouldn't be"
argument_list|,
name|token
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|String
name|expectedType
init|=
operator|(
name|String
operator|)
name|tcm
operator|.
name|get
argument_list|(
name|tokText
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"expectedType is null and it shouldn't be for: "
operator|+
name|token
argument_list|,
name|expectedType
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|token
operator|.
name|type
argument_list|()
operator|+
literal|" is not equal to "
operator|+
name|expectedType
operator|+
literal|" for "
operator|+
name|token
argument_list|,
name|token
operator|.
name|type
argument_list|()
operator|.
name|equals
argument_list|(
name|expectedType
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|type
argument_list|()
operator|.
name|equals
argument_list|(
name|WikipediaTokenizer
operator|.
name|ITALICS
argument_list|)
operator|==
literal|true
condition|)
block|{
name|numItalics
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|type
argument_list|()
operator|.
name|equals
argument_list|(
name|WikipediaTokenizer
operator|.
name|BOLD_ITALICS
argument_list|)
operator|==
literal|true
condition|)
block|{
name|numBoldItalics
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|type
argument_list|()
operator|.
name|equals
argument_list|(
name|WikipediaTokenizer
operator|.
name|CATEGORY
argument_list|)
operator|==
literal|true
condition|)
block|{
name|numCategory
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|type
argument_list|()
operator|.
name|equals
argument_list|(
name|WikipediaTokenizer
operator|.
name|CITATION
argument_list|)
operator|==
literal|true
condition|)
block|{
name|numCitation
operator|++
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"We have not seen enough tokens: "
operator|+
name|count
operator|+
literal|" is not>= "
operator|+
name|tcm
operator|.
name|size
argument_list|()
argument_list|,
name|count
operator|>=
name|tcm
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|numItalics
operator|+
literal|" does not equal: "
operator|+
literal|4
operator|+
literal|" for numItalics"
argument_list|,
name|numItalics
operator|==
literal|4
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|numBoldItalics
operator|+
literal|" does not equal: "
operator|+
literal|3
operator|+
literal|" for numBoldItalics"
argument_list|,
name|numBoldItalics
operator|==
literal|3
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|numCategory
operator|+
literal|" does not equal: "
operator|+
literal|10
operator|+
literal|" for numCategory"
argument_list|,
name|numCategory
operator|==
literal|10
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|numCitation
operator|+
literal|" does not equal: "
operator|+
literal|1
operator|+
literal|" for numCitation"
argument_list|,
name|numCitation
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testLinkPhrases
specifier|public
name|void
name|testLinkPhrases
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|test
init|=
literal|"click [[link here again]] click [http://lucene.apache.org here again]"
decl_stmt|;
name|WikipediaTokenizer
name|tf
init|=
operator|new
name|WikipediaTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|test
argument_list|)
argument_list|)
decl_stmt|;
name|Token
name|token
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
name|token
operator|=
name|tf
operator|.
name|next
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"token is null and it shouldn't be"
argument_list|,
name|token
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
operator|+
literal|" is not equal to "
operator|+
literal|"click"
argument_list|,
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
literal|"click"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|token
operator|.
name|getPositionIncrement
argument_list|()
operator|+
literal|" does not equal: "
operator|+
literal|1
argument_list|,
name|token
operator|.
name|getPositionIncrement
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|token
operator|=
name|tf
operator|.
name|next
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"token is null and it shouldn't be"
argument_list|,
name|token
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
operator|+
literal|" is not equal to "
operator|+
literal|"link"
argument_list|,
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
literal|"link"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|token
operator|.
name|getPositionIncrement
argument_list|()
operator|+
literal|" does not equal: "
operator|+
literal|1
argument_list|,
name|token
operator|.
name|getPositionIncrement
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|token
operator|=
name|tf
operator|.
name|next
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"token is null and it shouldn't be"
argument_list|,
name|token
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
operator|+
literal|" is not equal to "
operator|+
literal|"here"
argument_list|,
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
literal|"here"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
comment|//The link, and here should be at the same position for phrases to work
name|assertTrue
argument_list|(
name|token
operator|.
name|getPositionIncrement
argument_list|()
operator|+
literal|" does not equal: "
operator|+
literal|0
argument_list|,
name|token
operator|.
name|getPositionIncrement
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|token
operator|=
name|tf
operator|.
name|next
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"token is null and it shouldn't be"
argument_list|,
name|token
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
operator|+
literal|" is not equal to "
operator|+
literal|"again"
argument_list|,
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
literal|"again"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|token
operator|.
name|getPositionIncrement
argument_list|()
operator|+
literal|" does not equal: "
operator|+
literal|1
argument_list|,
name|token
operator|.
name|getPositionIncrement
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|token
operator|=
name|tf
operator|.
name|next
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"token is null and it shouldn't be"
argument_list|,
name|token
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
operator|+
literal|" is not equal to "
operator|+
literal|"click"
argument_list|,
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
literal|"click"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|token
operator|.
name|getPositionIncrement
argument_list|()
operator|+
literal|" does not equal: "
operator|+
literal|1
argument_list|,
name|token
operator|.
name|getPositionIncrement
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|token
operator|=
name|tf
operator|.
name|next
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"token is null and it shouldn't be"
argument_list|,
name|token
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
operator|+
literal|" is not equal to "
operator|+
literal|"http://lucene.apache.org"
argument_list|,
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
literal|"http://lucene.apache.org"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|token
operator|.
name|getPositionIncrement
argument_list|()
operator|+
literal|" does not equal: "
operator|+
literal|1
argument_list|,
name|token
operator|.
name|getPositionIncrement
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|token
operator|=
name|tf
operator|.
name|next
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"token is null and it shouldn't be"
argument_list|,
name|token
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
operator|+
literal|" is not equal to "
operator|+
literal|"here"
argument_list|,
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
literal|"here"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|token
operator|.
name|getPositionIncrement
argument_list|()
operator|+
literal|" does not equal: "
operator|+
literal|0
argument_list|,
name|token
operator|.
name|getPositionIncrement
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|token
operator|=
name|tf
operator|.
name|next
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"token is null and it shouldn't be"
argument_list|,
name|token
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
operator|+
literal|" is not equal to "
operator|+
literal|"again"
argument_list|,
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
literal|"again"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|token
operator|.
name|getPositionIncrement
argument_list|()
operator|+
literal|" does not equal: "
operator|+
literal|1
argument_list|,
name|token
operator|.
name|getPositionIncrement
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testLinks
specifier|public
name|void
name|testLinks
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|test
init|=
literal|"[http://lucene.apache.org/java/docs/index.html#news here] [http://lucene.apache.org/java/docs/index.html?b=c here] [https://lucene.apache.org/java/docs/index.html?b=c here]"
decl_stmt|;
name|WikipediaTokenizer
name|tf
init|=
operator|new
name|WikipediaTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|test
argument_list|)
argument_list|)
decl_stmt|;
name|Token
name|token
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
name|token
operator|=
name|tf
operator|.
name|next
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"token is null and it shouldn't be"
argument_list|,
name|token
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
operator|+
literal|" is not equal to "
operator|+
literal|"http://lucene.apache.org/java/docs/index.html#news"
argument_list|,
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
literal|"http://lucene.apache.org/java/docs/index.html#news"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|token
operator|.
name|type
argument_list|()
operator|+
literal|" is not equal to "
operator|+
name|WikipediaTokenizer
operator|.
name|EXTERNAL_LINK_URL
argument_list|,
name|token
operator|.
name|type
argument_list|()
operator|.
name|equals
argument_list|(
name|WikipediaTokenizer
operator|.
name|EXTERNAL_LINK_URL
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|tf
operator|.
name|next
argument_list|(
name|token
argument_list|)
expr_stmt|;
comment|//skip here
name|token
operator|=
name|tf
operator|.
name|next
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"token is null and it shouldn't be"
argument_list|,
name|token
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
operator|+
literal|" is not equal to "
operator|+
literal|"http://lucene.apache.org/java/docs/index.html?b=c"
argument_list|,
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
literal|"http://lucene.apache.org/java/docs/index.html?b=c"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|token
operator|.
name|type
argument_list|()
operator|+
literal|" is not equal to "
operator|+
name|WikipediaTokenizer
operator|.
name|EXTERNAL_LINK_URL
argument_list|,
name|token
operator|.
name|type
argument_list|()
operator|.
name|equals
argument_list|(
name|WikipediaTokenizer
operator|.
name|EXTERNAL_LINK_URL
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|tf
operator|.
name|next
argument_list|(
name|token
argument_list|)
expr_stmt|;
comment|//skip here
name|token
operator|=
name|tf
operator|.
name|next
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"token is null and it shouldn't be"
argument_list|,
name|token
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
operator|+
literal|" is not equal to "
operator|+
literal|"https://lucene.apache.org/java/docs/index.html?b=c"
argument_list|,
operator|new
name|String
argument_list|(
name|token
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|termLength
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
literal|"https://lucene.apache.org/java/docs/index.html?b=c"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|token
operator|.
name|type
argument_list|()
operator|+
literal|" is not equal to "
operator|+
name|WikipediaTokenizer
operator|.
name|EXTERNAL_LINK_URL
argument_list|,
name|token
operator|.
name|type
argument_list|()
operator|.
name|equals
argument_list|(
name|WikipediaTokenizer
operator|.
name|EXTERNAL_LINK_URL
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


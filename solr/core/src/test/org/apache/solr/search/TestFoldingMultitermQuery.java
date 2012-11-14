begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrTestCaseJ4
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestFoldingMultitermQuery
specifier|public
class|class
name|TestFoldingMultitermQuery
extends|extends
name|SolrTestCaseJ4
block|{
DECL|method|getCoreName
specifier|public
name|String
name|getCoreName
parameter_list|()
block|{
return|return
literal|"basic"
return|;
block|}
annotation|@
name|BeforeClass
DECL|method|beforeTests
specifier|public
specifier|static
name|void
name|beforeTests
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema-folding.xml"
argument_list|)
expr_stmt|;
name|String
name|docs
index|[]
init|=
block|{
literal|"abcdefg1 finger"
block|,
literal|"gangs hijklmn1"
block|,
literal|"opqrstu1 zilly"
block|,     }
decl_stmt|;
comment|// prepare the index
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|docs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|num
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|boolVal
init|=
operator|(
operator|(
name|i
operator|%
literal|2
operator|)
operator|==
literal|0
operator|)
condition|?
literal|"true"
else|:
literal|"false"
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|num
argument_list|,
literal|"int_f"
argument_list|,
name|num
argument_list|,
literal|"float_f"
argument_list|,
name|num
argument_list|,
literal|"long_f"
argument_list|,
name|num
argument_list|,
literal|"double_f"
argument_list|,
name|num
argument_list|,
literal|"byte_f"
argument_list|,
name|num
argument_list|,
literal|"short_f"
argument_list|,
name|num
argument_list|,
literal|"bool_f"
argument_list|,
name|boolVal
argument_list|,
literal|"date_f"
argument_list|,
literal|"200"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|i
operator|%
literal|10
argument_list|)
operator|+
literal|"-01-01T00:00:00Z"
argument_list|,
literal|"content"
argument_list|,
name|docs
index|[
name|i
index|]
argument_list|,
literal|"content_ws"
argument_list|,
name|docs
index|[
name|i
index|]
argument_list|,
literal|"content_rev"
argument_list|,
name|docs
index|[
name|i
index|]
argument_list|,
literal|"content_multi"
argument_list|,
name|docs
index|[
name|i
index|]
argument_list|,
literal|"content_lower_token"
argument_list|,
name|docs
index|[
name|i
index|]
argument_list|,
literal|"content_oldstyle"
argument_list|,
name|docs
index|[
name|i
index|]
argument_list|,
literal|"content_charfilter"
argument_list|,
name|docs
index|[
name|i
index|]
argument_list|,
literal|"content_multi_bad"
argument_list|,
name|docs
index|[
name|i
index|]
argument_list|,
literal|"content_straight"
argument_list|,
name|docs
index|[
name|i
index|]
argument_list|,
literal|"content_lower"
argument_list|,
name|docs
index|[
name|i
index|]
argument_list|,
literal|"content_folding"
argument_list|,
name|docs
index|[
name|i
index|]
argument_list|,
literal|"content_stemming"
argument_list|,
name|docs
index|[
name|i
index|]
argument_list|,
literal|"content_keyword"
argument_list|,
name|docs
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Mixing and matching amongst various languages is probalby a bad thing, so add some tests for various
comment|// special filters
name|int
name|idx
init|=
name|docs
operator|.
name|length
decl_stmt|;
comment|// Greek
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_greek"
argument_list|,
literal|"ÎÎ¬ÏÎ¿Ï"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_greek"
argument_list|,
literal|"ÎÎÎªÎÎ£"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Turkish
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_turkish"
argument_list|,
literal|"\u0130STANBUL"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_turkish"
argument_list|,
literal|"ISPARTA"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_turkish"
argument_list|,
literal|"izmir"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Russian normalization
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_russian"
argument_list|,
literal|"ÑÐ»ÐµÐºÑÑÐ¾Ð¼Ð°Ð³Ð½Ð¸ÑÐ½Ð¾Ð¹"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_russian"
argument_list|,
literal|"ÐÐ¼ÐµÑÑÐµ"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_russian"
argument_list|,
literal|"ÑÐ¸Ð»Ðµ"
argument_list|)
argument_list|)
expr_stmt|;
comment|// persian normalization
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_persian"
argument_list|,
literal|"ÙØ§Ù"
argument_list|)
argument_list|)
expr_stmt|;
comment|// arabic normalization
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_arabic"
argument_list|,
literal|"Ø±ÙØ¨Ø±Øª"
argument_list|)
argument_list|)
expr_stmt|;
comment|// hindi normalization
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_hindi"
argument_list|,
literal|"à¤¹à¤¿à¤à¤¦à¥"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_hindi"
argument_list|,
literal|"à¤à¤¾à¤à¤¾"
argument_list|)
argument_list|)
expr_stmt|;
comment|// german normalization
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_german"
argument_list|,
literal|"weissbier"
argument_list|)
argument_list|)
expr_stmt|;
comment|// cjk width normalization
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|idx
operator|++
argument_list|)
argument_list|,
literal|"content_width"
argument_list|,
literal|"ï½³ï¾ï½¨ï½¯ï¾"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPrefixCaseAccentFolding
specifier|public
name|void
name|testPrefixCaseAccentFolding
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|matchOneDocPrefixUpper
index|[]
index|[]
init|=
block|{
block|{
literal|"A*"
block|,
literal|"ÃB*"
block|,
literal|"ABÃ*"
block|}
block|,
comment|// these should find only doc 0
block|{
literal|"H*"
block|,
literal|"HÃ*"
block|,
literal|"HÃ¬J*"
block|}
block|,
comment|// these should find only doc 1
block|{
literal|"O*"
block|,
literal|"ÃP*"
block|,
literal|"OPQ*"
block|}
block|,
comment|// these should find only doc 2
block|}
decl_stmt|;
name|String
name|matchRevPrefixUpper
index|[]
index|[]
init|=
block|{
block|{
literal|"*Ä1"
block|,
literal|"*DEfG1"
block|,
literal|"*EfG1"
block|}
block|,
block|{
literal|"*N1"
block|,
literal|"*LmÅ1"
block|,
literal|"*MÃ1"
block|}
block|,
block|{
literal|"*Ç1"
block|,
literal|"*sTu1"
block|,
literal|"*RÅ TU1"
block|}
block|}
decl_stmt|;
comment|// test the prefix queries find only one doc where the query is uppercased. Must go through query parser here!
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|matchOneDocPrefixUpper
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
for|for
control|(
name|int
name|jdx
init|=
literal|0
init|;
name|jdx
operator|<
name|matchOneDocPrefixUpper
index|[
name|idx
index|]
operator|.
name|length
condition|;
name|jdx
operator|++
control|)
block|{
name|String
name|me
init|=
name|matchOneDocPrefixUpper
index|[
name|idx
index|]
index|[
name|jdx
index|]
decl_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content:"
operator|+
name|me
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//*[@name='id'][.='"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|idx
argument_list|)
operator|+
literal|"']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_ws:"
operator|+
name|me
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//*[@name='id'][.='"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|idx
argument_list|)
operator|+
literal|"']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_multi:"
operator|+
name|me
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//*[@name='id'][.='"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|idx
argument_list|)
operator|+
literal|"']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_lower_token:"
operator|+
name|me
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|,
literal|"//*[@name='id'][.='"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|idx
argument_list|)
operator|+
literal|"']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_oldstyle:"
operator|+
name|me
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|matchRevPrefixUpper
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
for|for
control|(
name|int
name|jdx
init|=
literal|0
init|;
name|jdx
operator|<
name|matchRevPrefixUpper
index|[
name|idx
index|]
operator|.
name|length
condition|;
name|jdx
operator|++
control|)
block|{
name|String
name|me
init|=
name|matchRevPrefixUpper
index|[
name|idx
index|]
index|[
name|jdx
index|]
decl_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_rev:"
operator|+
name|me
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//*[@name='id'][.='"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|idx
argument_list|)
operator|+
literal|"']"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// test the wildcard queries find only one doc  where the query is uppercased and/or accented.
annotation|@
name|Test
DECL|method|testWildcardCaseAccentFolding
specifier|public
name|void
name|testWildcardCaseAccentFolding
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|matchOneDocWildUpper
index|[]
index|[]
init|=
block|{
block|{
literal|"Ã*C*"
block|,
literal|"ÃB*1"
block|,
literal|"ABÃ*g1"
block|,
literal|"Ã*FG1"
block|}
block|,
comment|// these should find only doc 0
block|{
literal|"H*k*"
block|,
literal|"HÃ*l?*"
block|,
literal|"HÃ¬J*n*"
block|,
literal|"HÃ¬J*m*"
block|}
block|,
comment|// these should find only doc 1
block|{
literal|"O*Å*"
block|,
literal|"ÃP*Å???"
block|,
literal|"OPQ*S?Å®*"
block|,
literal|"ÃP*1"
block|}
block|,
comment|// these should find only doc 2
block|}
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|matchOneDocWildUpper
operator|.
name|length
condition|;
name|idx
operator|++
control|)
block|{
for|for
control|(
name|int
name|jdx
init|=
literal|0
init|;
name|jdx
operator|<
name|matchOneDocWildUpper
index|[
name|idx
index|]
operator|.
name|length
condition|;
name|jdx
operator|++
control|)
block|{
name|String
name|me
init|=
name|matchOneDocWildUpper
index|[
name|idx
index|]
index|[
name|jdx
index|]
decl_stmt|;
name|assertQ
argument_list|(
literal|"Error with "
operator|+
name|me
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content:"
operator|+
name|me
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|,
literal|"//*[@name='id'][.='"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|idx
argument_list|)
operator|+
literal|"']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_ws:"
operator|+
name|me
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|,
literal|"//*[@name='id'][.='"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|idx
argument_list|)
operator|+
literal|"']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_multi:"
operator|+
name|me
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|,
literal|"//*[@name='id'][.='"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|idx
argument_list|)
operator|+
literal|"']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_oldstyle:"
operator|+
name|me
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testLowerTokenizer
specifier|public
name|void
name|testLowerTokenizer
parameter_list|()
block|{
comment|// The lowercasetokenizer will remove the '1' from the index, but not from the query, thus the special test.
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_lower_token:Ã*C*"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_lower_token:Ã*C*1"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_lower_token:h*1"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_lower_token:H*1"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_lower_token:*1"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_lower_token:HÃ*l?*"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_lower_token:hÈ*l?*"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFuzzy
specifier|public
name|void
name|testFuzzy
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content:ZiLLx~1"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_straight:ZiLLx~1"
argument_list|)
argument_list|,
comment|// case preserving field shouldn't match
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_folding:ZiLLx~1"
argument_list|)
argument_list|,
comment|// case preserving field shouldn't match
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRegex
specifier|public
name|void
name|testRegex
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content:/Zill[a-z]/"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content:/Zill[A-Z]/"
argument_list|)
argument_list|,
comment|// everything in the regex gets lowercased?
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_keyword:/.*Zill[A-Z]/"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_straight:/Zill[a-z]/"
argument_list|)
argument_list|,
comment|// case preserving field shouldn't match
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_folding:/Zill[a-z]/"
argument_list|)
argument_list|,
comment|// case preserving field shouldn't match
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_keyword:/Abcdefg1 Finger/"
argument_list|)
argument_list|,
comment|// test spaces
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGeneral
specifier|public
name|void
name|testGeneral
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_stemming:fings*"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
comment|// should not match (but would if fings* was stemmed to fing*
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_stemming:fing*"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
comment|// Phrases should fail. This test is mainly a marker so if phrases ever do start working with wildcards we go
comment|// and update the documentation
annotation|@
name|Test
DECL|method|testPhrase
specifier|public
name|void
name|testPhrase
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content:\"silly ABCD*\""
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWildcardRange
specifier|public
name|void
name|testWildcardRange
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content:[* TO *]"
argument_list|)
argument_list|,
literal|"//result[@numFound='3']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content:[AB* TO Z*]"
argument_list|)
argument_list|,
literal|"//result[@numFound='3']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content:[AB*E?G* TO TU*W]"
argument_list|)
argument_list|,
literal|"//result[@numFound='3']"
argument_list|)
expr_stmt|;
block|}
comment|// Does the char filter get correctly handled?
annotation|@
name|Test
DECL|method|testCharFilter
specifier|public
name|void
name|testCharFilter
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_charfilter:"
operator|+
literal|"Ã*C*"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|,
literal|"//*[@name='id'][.='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_charfilter:"
operator|+
literal|"ABÃ*g1"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|,
literal|"//*[@name='id'][.='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_charfilter:"
operator|+
literal|"HÃ*l?*"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|,
literal|"//*[@name='id'][.='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRangeQuery
specifier|public
name|void
name|testRangeQuery
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content:"
operator|+
literal|"{Èªp*1 TO QÅ®*}"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|,
literal|"//*[@name='id'][.='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content:"
operator|+
literal|"[Ãb* TO f?Ãg?r]"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|,
literal|"//*[@name='id'][.='0']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNonTextTypes
specifier|public
name|void
name|testNonTextTypes
parameter_list|()
block|{
name|String
index|[]
name|intTypes
init|=
block|{
literal|"int_f"
block|,
literal|"float_f"
block|,
literal|"long_f"
block|,
literal|"double_f"
block|,
literal|"byte_f"
block|,
literal|"short_f"
block|}
decl_stmt|;
for|for
control|(
name|String
name|str
range|:
name|intTypes
control|)
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|str
operator|+
literal|":"
operator|+
literal|"0"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|,
literal|"//*[@name='id'][.='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|str
operator|+
literal|":"
operator|+
literal|"[0 TO 2]"
argument_list|)
argument_list|,
literal|"//result[@numFound='3']"
argument_list|,
literal|"//*[@name='id'][.='0']"
argument_list|,
literal|"//*[@name='id'][.='1']"
argument_list|,
literal|"//*[@name='id'][.='2']"
argument_list|)
expr_stmt|;
block|}
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"bool_f:true"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|,
literal|"//*[@name='id'][.='0']"
argument_list|,
literal|"//*[@name='id'][.='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"bool_f:[false TO true]"
argument_list|)
argument_list|,
literal|"//result[@numFound='3']"
argument_list|,
literal|"//*[@name='id'][.='0']"
argument_list|,
literal|"//*[@name='id'][.='1']"
argument_list|,
literal|"//*[@name='id'][.='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"date_f:2000-01-01T00\\:00\\:00Z"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|,
literal|"//*[@name='id'][.='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"date_f:[2000-12-31T23:59:59.999Z TO 2002-01-02T00:00:01Z]"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|,
literal|"//*[@name='id'][.='1']"
argument_list|,
literal|"//*[@name='id'][.='2']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiBad
specifier|public
name|void
name|testMultiBad
parameter_list|()
block|{
try|try
block|{
name|ignoreException
argument_list|(
literal|"analyzer returned too many terms"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_multi_bad:"
operator|+
literal|"abCD*"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should throw exception when token evaluates to more than one term"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getCause
argument_list|()
operator|instanceof
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGreek
specifier|public
name|void
name|testGreek
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_greek:Î¼Î±Î¹Î¿*"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_greek:ÎÎÎªÎ*"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_greek:ÎÎ¬ÏÎ¿*"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRussian
specifier|public
name|void
name|testRussian
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_russian:ÑÐ»ÐÐºÑÐ Ð¾Ð¼Ð°Ð³Ð½*ÑÐ½Ð¾Ð¹"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_russian:ÐÐ¼Ðµ*ÑÐµ"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_russian:Ð¡Ð¸*Ðµ"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_russian:ÑÐÐµÐºÑÑÐ¾Ð¼Ð°Ð³Ð½ÐÑ*"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testPersian
specifier|public
name|void
name|testPersian
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_persian:ÙØ§Û*"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testArabic
specifier|public
name|void
name|testArabic
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_arabic:Ø±ÙØ¨Ø±ÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙÙØª*"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testHindi
specifier|public
name|void
name|testHindi
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_hindi:à¤¹à¤¿à¤¨à¥à¤¦à¥*"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_hindi:à¤à¤*"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testGerman
specifier|public
name|void
name|testGerman
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_german:weiÃ*"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testCJKWidth
specifier|public
name|void
name|testCJKWidth
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"content_width:ã´ã£*"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


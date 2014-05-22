begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
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
name|commons
operator|.
name|io
operator|.
name|FileUtils
import|;
end_import

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
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_comment
comment|/**  * Tests expert options of {@link ICUCollationField}.  */
end_comment

begin_class
DECL|class|TestICUCollationFieldOptions
specifier|public
class|class
name|TestICUCollationFieldOptions
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|testHome
init|=
name|createTempDir
argument_list|()
decl_stmt|;
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
name|getFile
argument_list|(
literal|"analysis-extras/solr"
argument_list|)
argument_list|,
name|testHome
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig-icucollate.xml"
argument_list|,
literal|"schema-icucollateoptions.xml"
argument_list|,
name|testHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
comment|// add some docs
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"text"
argument_list|,
literal|"foo-bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"text"
argument_list|,
literal|"foo bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"text"
argument_list|,
literal|"foobar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"text"
argument_list|,
literal|"foobar-10"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"text"
argument_list|,
literal|"foobar-9"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
literal|"text"
argument_list|,
literal|"resume"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|,
literal|"text"
argument_list|,
literal|"RÃ©sumÃ©"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"8"
argument_list|,
literal|"text"
argument_list|,
literal|"Resume"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"9"
argument_list|,
literal|"text"
argument_list|,
literal|"rÃ©sumÃ©"
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
comment|/*    * Setting alternate=shifted to shift whitespace, punctuation and symbols    * to quaternary level     */
DECL|method|testIgnorePunctuation
specifier|public
name|void
name|testIgnorePunctuation
parameter_list|()
block|{
name|assertQ
argument_list|(
literal|"Collated TQ: "
argument_list|,
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"sort_ignore_punctuation:foobar"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.=1]"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][.=2]"
argument_list|,
literal|"//result/doc[3]/int[@name='id'][.=3]"
argument_list|)
expr_stmt|;
block|}
comment|/*    * Setting alternate=shifted and variableTop to shift whitespace, but not     * punctuation or symbols, to quaternary level     */
DECL|method|testIgnoreWhitespace
specifier|public
name|void
name|testIgnoreWhitespace
parameter_list|()
block|{
name|assertQ
argument_list|(
literal|"Collated TQ: "
argument_list|,
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"sort_ignore_space:\"foo bar\""
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.=2]"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][.=3]"
argument_list|)
expr_stmt|;
block|}
comment|/*    * Setting numeric to encode digits with numeric value, so that    * foobar-9 sorts before foobar-10    */
DECL|method|testNumerics
specifier|public
name|void
name|testNumerics
parameter_list|()
block|{
name|assertQ
argument_list|(
literal|"Collated sort: "
argument_list|,
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"id:[4 TO 5]"
argument_list|,
literal|"sort"
argument_list|,
literal|"sort_numerics asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.=5]"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][.=4]"
argument_list|)
expr_stmt|;
block|}
comment|/*    * Setting caseLevel=true to create an additional case level between    * secondary and tertiary    */
DECL|method|testIgnoreAccentsButNotCase
specifier|public
name|void
name|testIgnoreAccentsButNotCase
parameter_list|()
block|{
name|assertQ
argument_list|(
literal|"Collated TQ: "
argument_list|,
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"sort_ignore_accents:resume"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.=6]"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][.=9]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Collated TQ: "
argument_list|,
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"sort_ignore_accents:Resume"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.=7]"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][.=8]"
argument_list|)
expr_stmt|;
block|}
comment|/*    * Setting caseFirst=upper to cause uppercase strings to sort    * before lowercase ones.    */
DECL|method|testUpperCaseFirst
specifier|public
name|void
name|testUpperCaseFirst
parameter_list|()
block|{
name|assertQ
argument_list|(
literal|"Collated sort: "
argument_list|,
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"id:6 OR id:8"
argument_list|,
literal|"sort"
argument_list|,
literal|"sort_uppercase_first asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.=8]"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][.=6]"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


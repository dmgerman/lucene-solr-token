begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
package|;
end_package

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|GregorianCalendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

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
name|junit
operator|.
name|framework
operator|.
name|TestSuite
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|textui
operator|.
name|TestRunner
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
name|store
operator|.
name|*
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
name|document
operator|.
name|*
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
name|*
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
name|*
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
name|*
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
name|*
import|;
end_import

begin_comment
comment|/** JUnit adaptation of an older test case SearchTest.  * @author dmitrys@earthlink.net  * @version $Id$  */
end_comment

begin_class
DECL|class|TestSearch
specifier|public
class|class
name|TestSearch
extends|extends
name|TestCase
block|{
comment|/** Main for running test case by itself. */
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
block|{
name|TestRunner
operator|.
name|run
argument_list|(
operator|new
name|TestSuite
argument_list|(
name|TestSearch
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** This test performs a number of searches. It also compares output      *  of searches using multi-file index segments with single-file      *  index segments.       *        *  TODO: someone should check that the results of the searches are      *        still correct by adding assert statements. Right now, the test      *        passes if the results are the same between multi-file and      *        single-file formats, even if the results are wrong.      */
DECL|method|testSearch
specifier|public
name|void
name|testSearch
parameter_list|()
throws|throws
name|Exception
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|doTestSearch
argument_list|(
name|pw
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|pw
operator|.
name|close
argument_list|()
expr_stmt|;
name|sw
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|multiFileOutput
init|=
name|sw
operator|.
name|getBuffer
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|multiFileOutput
argument_list|)
expr_stmt|;
name|sw
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|pw
operator|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|doTestSearch
argument_list|(
name|pw
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|pw
operator|.
name|close
argument_list|()
expr_stmt|;
name|sw
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|singleFileOutput
init|=
name|sw
operator|.
name|getBuffer
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|multiFileOutput
argument_list|,
name|singleFileOutput
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestSearch
specifier|private
name|void
name|doTestSearch
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|boolean
name|useCompoundFile
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|directory
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|SimpleAnalyzer
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|analyzer
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|writer
operator|.
name|setUseCompoundFile
argument_list|(
name|useCompoundFile
argument_list|)
expr_stmt|;
name|String
index|[]
name|docs
init|=
block|{
literal|"a b c d e"
block|,
literal|"a b c d e a b c d e"
block|,
literal|"a b c d e f g h i j"
block|,
literal|"a c e"
block|,
literal|"e c a"
block|,
literal|"a c e a c e"
block|,
literal|"a c e a b c"
block|}
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|docs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
name|Field
operator|.
name|Text
argument_list|(
literal|"contents"
argument_list|,
name|docs
index|[
name|j
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|Searcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|String
index|[]
name|queries
init|=
block|{
literal|"a b"
block|,
literal|"\"a b\""
block|,
literal|"\"a b c\""
block|,
literal|"a c"
block|,
literal|"\"a c\""
block|,
literal|"\"a c e\""
block|,       }
decl_stmt|;
name|Hits
name|hits
init|=
literal|null
decl_stmt|;
name|QueryParser
name|parser
init|=
operator|new
name|QueryParser
argument_list|(
literal|"contents"
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|parser
operator|.
name|setPhraseSlop
argument_list|(
literal|4
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|queries
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|Query
name|query
init|=
name|parser
operator|.
name|parse
argument_list|(
name|queries
index|[
name|j
index|]
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"Query: "
operator|+
name|query
operator|.
name|toString
argument_list|(
literal|"contents"
argument_list|)
argument_list|)
expr_stmt|;
comment|//DateFilter filter =
comment|//  new DateFilter("modified", Time(1997,0,1), Time(1998,0,1));
comment|//DateFilter filter = DateFilter.Before("modified", Time(1997,00,01));
comment|//System.out.println(filter);
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
name|hits
operator|.
name|length
argument_list|()
operator|+
literal|" total results"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hits
operator|.
name|length
argument_list|()
operator|&&
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|d
init|=
name|hits
operator|.
name|doc
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
name|i
operator|+
literal|" "
operator|+
name|hits
operator|.
name|score
argument_list|(
name|i
argument_list|)
comment|// 			   + " " + DateField.stringToDate(d.get("modified"))
operator|+
literal|" "
operator|+
name|d
operator|.
name|get
argument_list|(
literal|"contents"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|Time
specifier|static
name|long
name|Time
parameter_list|(
name|int
name|year
parameter_list|,
name|int
name|month
parameter_list|,
name|int
name|day
parameter_list|)
block|{
name|GregorianCalendar
name|calendar
init|=
operator|new
name|GregorianCalendar
argument_list|()
decl_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|year
argument_list|,
name|month
argument_list|,
name|day
argument_list|)
expr_stmt|;
return|return
name|calendar
operator|.
name|getTime
argument_list|()
operator|.
name|getTime
argument_list|()
return|;
block|}
block|}
end_class

end_unit


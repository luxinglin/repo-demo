package com.gary.inc
import org.apache.spark.{SparkConf, SparkContext}

import org.apache.spark.SparkContext._

case class PBInfo(name: String, sex: String, age: Int, pf: String)

object SparkTest {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("sptest").setMaster("local[*]")
      .set("spark.eventLog.enabled", "false")
      .set("spark.eventLog.compress", "false")
    val ssc = new SparkContext(sparkConf)

    println("��RDD:")
    val rdd = ssc.parallelize(Seq(1, 2, 3, 4, 5, 6))
    println("result:")
    rdd.filter(_ < 3).map(_ * 2).foreach(println)
    println("��ȡ�ļ�:")
    //"hdfs://10.0.72.64:9000/hbase/rs600/bizRem.txt"
    val txtRdd = ssc.textFile("C:\\Work\\90.TEMP\\IDEA_WorkSpace\\repo-demo\\src\\man\\scala\\com\\gary\\inc\\str.txt")

    println("������" + txtRdd.count())
    var idx = 0
    txtRdd.foreach(item => {
      idx += 1
      println(idx + "::" + Thread.currentThread().getName + "::" + item.toString)
    })

    println("�����Կո��� ��> ��ÿ�����ʴ��ϸ�������ɵ���(word,1) -> ������� -> ������������ ->������ӡ")
    txtRdd.flatMap(line => line.split(" ")).map(word => (word, 1)).reduceByKey((a, b) => a + b).sortBy(_._2).foreach(println)

    val sqlContext = new org.apache.spark.sql.SQLContext(ssc)
    import sqlContext._
    val tbRdd = ssc.textFile("C:\\Work\\90.TEMP\\IDEA_WorkSpace\\repo-demo\\src\\man\\scala\\com\\gary\\inc\\tb.txt")
    val personRdd = tbRdd.map(row => {
      val cols = row.split(" ")
      PBInfo(cols(0), cols(1), cols(2).toInt, cols(3))
    })
    personRdd.registerTempTable("Tb_PBInfo")
    //   sqlContext.sparkContext.parallelize(tbRdd).registerTempTable("logInfo")
    println("\n��ѯ����:")
    sqlContext.sql("select * from Tb_PBInfo").collect().foreach(println)
    println("\n����������ʹ���18:")
    sqlContext.sql("select * from Tb_PBInfo where age > 18").collect().foreach(println)
    println("\n�������в�Ϊ��ũ������:")
    sqlContext.sql("select * from Tb_PBInfo where pf != '��ũ' and sex = 'Ů'").collect().foreach(println)
    ssc.stop()
  }
}


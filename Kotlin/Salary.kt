/* 
********************
Chua, Judy P.
Language: Kotlin
Paradigm(s): Kotlin embraces multiple paradigms such as object-oriented, functional, 
             imperative, and procedural programming, considering that the language is 
             identical to numerous languages like C#, Java, and many more.
********************
*/

class Settings(var employeeNumber: Int, var salary: Double, var maxHours: Int, var workDay: Int, 
               var restDay: Int, var typeDay: MutableMap<String, String>, 
               var inDay: MutableMap<String, String>, var outDay: MutableMap<String, String>) {
    fun newSalary(newSalary: Double) {
        salary = newSalary
    }
    fun newMaxHours(newMaxHours: Int) {
        maxHours = newMaxHours
    }
    fun newWorkDay(newWorkDay: Int) {
        workDay = newWorkDay
        restDay = 7 - workDay
    }
    fun specificTypeDay(day: String, type: String) {
        typeDay[day] = type
    }
    fun specificInDay(day: String, time: String) {
        inDay[day] = time
    }
    fun specificOutDay(day: String, time: String) {
        outDay[day] = time
    }
}

//Checks if user inputs valid military time
fun isValidTime(time: Int): Boolean {
    var minute = time % 100
    var hour = time / 100

    if ((hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59) || time == 2400)
        return true
    else return false
}

//Compiles the list of hours for each categories
fun hoursBetween(inTime: Int, outTime: Int, maxHours: Int): List<Int> {
    var inHour = inTime / 100
    var numHour: Int = 0

    var isOT: Boolean
    var isNight: Boolean

    var nightHour = 0
    var hourOT = 0
    var nightOT = 0

    var listOfHours = mutableListOf(0) //initialize with 0

    while (inHour != outTime / 100) {
        if(numHour >= maxHours)
            isOT = true
        else isOT = false

        if (inHour >= 22 || (inHour >= 0 && inHour < 6))
            isNight = true
        else isNight = false

        if (!isOT && isNight) //night shift with no OT
            nightHour++
        else if (isOT && isNight) //night shift with OT
            nightOT++
        else if (isOT && !isNight) //OT
            hourOT++

        //next hour
        if (inHour >= 23)
            inHour = inHour - 23 //2300 -> 0000
        else inHour++
        numHour++
    }

    listOfHours.add(nightHour)
    listOfHours.add(hourOT)
    listOfHours.add(nightOT)
    return listOfHours
}

//Checks how many hours there are in between the times given
fun checkOutTime(inTime: Int, outTime: Int): Int {
    var inHour = inTime / 100
    var numHour: Int = 0
    while (inHour != outTime / 100) {
        //next hour
        if (inHour >= 23)
            inHour = inHour - 23 //2300 -> 0000
        else inHour++
        numHour++
    }
    return numHour
}

//Computes the salary of the employee and returns the list of hours and total
fun computation(config: Settings, day: Map<Int, String>, 
                dayNum: Int, outTime: Int): MutableList<Double> {
    
    var inTime = Integer.valueOf(config.inDay[day[dayNum]])
    var typeDay = config.typeDay[day[dayNum]]
    var dailySalary = config.salary
    var output = mutableListOf(dayNum.toDouble())

    var hourRate = config.salary / config.maxHours

    var addRate = 1.00
    var nightRate = 1.00
    var hourOTRate = 1.00
    var nightOTRate = 1.00

    //additional rate
    when (typeDay) {
        "Normal Day" -> {
            addRate = 1.00
            nightRate = 1.10
            hourOTRate = 1.25
            nightOTRate = 1.375
        }
        "Rest Day" -> {
            addRate = 1.30
            nightRate = 1.40
            hourOTRate = 1.69
            nightOTRate = 1.859
        }
        "Special Non-Working Day" -> {
            addRate = 1.30
            nightRate = 1.40
            hourOTRate = 1.69
            nightOTRate = 1.859
        }
        "Special Non-Working Day and Rest Day" -> {
            addRate = 1.50
            nightRate = 1.60
            hourOTRate = 1.95
            nightOTRate = 2.145
        }
        "Regular Holiday" -> {
            addRate = 2.00
            nightRate = 2.10
            hourOTRate = 2.60
            nightOTRate = 2.86
        }
        "Regular Holiday and Rest Day" -> {
            addRate = 2.60
            nightRate = 2.70
            hourOTRate = 3.38
            nightOTRate = 3.718
        }
    }

    //employee did not work
    if (inTime == outTime) {
        output.add(0.00) //Night Shift
        output.add(0.00) //OT
        output.add(0.00) //Night Shift OT
        if (typeDay.equals("Normal Day")) //employee is absent, return 0
            output.add(0.00) 
        else output.add(dailySalary) //Salary of the day
        return output
    } 
    
    dailySalary *= addRate

    var getHours = hoursBetween(inTime, outTime, config.maxHours+1)
    var nightHour = getHours.get(1).toDouble()
    var hourOT = getHours.get(2).toDouble()
    var nightOT = getHours.get(3).toDouble()

    if (nightHour > 0)
        dailySalary = dailySalary + nightHour * hourRate * nightRate

    if (hourOT > 0)
        dailySalary = dailySalary + hourOT * hourRate * hourOTRate

    if (nightOT > 0)
        dailySalary = dailySalary + nightOT * hourRate * nightOTRate
    
    output.add(nightHour) //Night Shift with no OT
    output.add(hourOT) //OT
    output.add(nightOT) //Night Shift OT
    output.add(dailySalary) //Salary of the day

    return output
}

//Displays the salary for each day and the overall total salary for the week
fun showSalaryWeek(salary: Map<String, Double>, id: Int) {
    val keys = salary.keys
    println("*****************************************")
    println("*          SALARY FOR THE WEEK          *")
    println("*****************************************")
    for (key in keys) {
        println(String.format("* %-11S | %19.2f PHP *", key, salary[key]))
    }
    println("*---------------------------------------*")
    println(String.format("* %-11S | %19.2f PHP *", "TOTAL", salary.values.sum()))
    println("*****************************************")
    println("   The total salary of Employee ${id} for the week is P${salary.values.sum()}")
}
 
//OPTION 1 Function
fun generatePayroll(set: Settings, getDay: Map<Int, String>) {
    var day: Int = 1 //number of days
    //store salary of employee for each day
    var salaryWeek = mutableMapOf("Monday"     to 0.00, 
        "Tuesday"    to 0.00,     "Wednesday"  to 0.00,
        "Thursday"   to 0.00,     "Friday"     to 0.00,
        "Saturday"   to 0.00,     "Sunday"     to 0.00)

    println()
    while (day < 8) {
        var dayTitle = String.format("%-9s%-10s", ">>>", getDay[day])
        println(dayTitle)
        println("Daily Rate  |  ${set.salary}")
        println("Day Type    |  ${set.typeDay[getDay[day]]}")
        println("IN TIME     |  ${set.inDay[getDay[day]]}")
        println("OUT TIME    |  ${set.outDay[getDay[day]]}")
        
        var inTime = Integer.valueOf(set.inDay[getDay[day]])
        var outTime = Integer.valueOf(set.outDay[getDay[day]])
        var numHours = checkOutTime(inTime, outTime)
        var changeOut: Char //response of user if changing out time
        //ask if change OUT time
        do {
            print("Change OUT Time? [Y/N] ")
            changeOut = readLine()?.get(0) ?: 'N' //returns null if no input, null is set to N
            if (changeOut != 'Y' && changeOut != 'y' && 
                changeOut != 'N' && changeOut != 'n')
                println("INVALID RESPONSE! Input Y for Yes or N for No.\n")
        } while (changeOut != 'Y' && changeOut != 'y' && 
                changeOut != 'N' && changeOut != 'n')
        
        //make sure valid OUT time
        if (changeOut == 'Y' || changeOut == 'y' || ((changeOut == 'N' || changeOut == 'n') && numHours < 8 && numHours != 0)) {
            if ((changeOut == 'N' || changeOut == 'n') && numHours < 8 && numHours != 0)
                println("ERROR: Employees must work for at least 8 hours. Need to change OUT time.\n")
            do {
                print("OUT TIME (HHMM) |  ")
                outTime = Integer.valueOf(readLine())
                numHours = checkOutTime(inTime, outTime)
                if (!isValidTime(outTime))
                    println("INVALID TIME! Input again (0000 - 2359).\n")
                else if (numHours < 8 && numHours > 0)
                    println("ERROR: Employees must work for at least 8 hours.\n")
            } while (!isValidTime(outTime) || (numHours < 9 && numHours > 0))
        }

        var newInfo = computation(set, getDay, day, outTime)
        var nightHour = newInfo.get(1)
        var hourOT = newInfo.get(2)
        var nightOT = newInfo.get(3)
        var total = newInfo.get(4)
        var totalPrint = String.format("%.2f", total)
        var salaryPrint = String.format("%.2f", set.salary)

        //display the whole thing in a cleaner version
        println("\n=====================================================")
        println("                     ${getDay[day]}")
        println("=====================================================")
        println("Daily Rate                           |  ${salaryPrint}")
        println("IN TIME                              |  ${set.inDay[getDay[day]]}")
        println("OUT TIME                             |  ${String.format("%04d", outTime)}")
        println("Day Type                             |  ${set.typeDay[getDay[day]]}")
        if (nightHour > 0)
            println("Hours on Night Shift                 |  ${String.format("%.0f", nightHour)}")
        println("Hours Overtime (Night Shift Overtime)|  ${String.format("%.0f", hourOT)} (${String.format("%.0f", nightOT)})")
        println("Salary of the day:                   |  ${totalPrint}")
        println("=====================================================\n\n")

        //store in the map
        salaryWeek[getDay[day] ?: "Error"] = total  //if getDay[day] is null, substitute is with Error
        day++ //next day
    }
    showSalaryWeek(salaryWeek, set.employeeNumber)
}

//displays the settings saved
fun printSettings(config: Settings, day: Map<Int, String>, title: String) {
    println("\n\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
    println("                           ${title}")
    println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
    var format = String.format("%.2f", config.salary)
    println(" > Daily Salary              ===   ${format}")
    println(" > Maximum Regular Hours     ===   ${config.maxHours}")
    println(" > Work Days -- Rest Days    ===   ${config.workDay} -- ${config.restDay}")
    println("-------------------------------------------------------------------------")
    println("    DAY    |               Day Type               | IN  TIME | OUT TIME |")
    println("-----------|--------------------------------------|----------|----------|")
    for (i in 1..7) {
        println(" " + String.format("%-9s", day[i]) + " | " + String.format("%-36s", config.typeDay[day[i]]) + 
                " |   " + String.format("%4s", config.inDay[day[i]]) + "   |   " + 
                String.format("%4s", config.outDay[day[i]]) + "   |")
    }
    println("-------------------------------------------------------------------------\n")
}

//prints option for days display
fun printOptDays() {
    println("   (1) Monday   (2) Tuesday    (3) Wednesday   (4) Thursday")
    println("   (5) Friday   (6) Saturday   (7) Sunday   (8) All   (9) None")
}

//prints option for day type display
fun printTypeDay(day: String) {
    println("-------------------NOTE-------------------")
    println(" {1} Normal Day")
    println(" {2} Rest Day")
    println(" {3} Speical Non-Working Day")
    println(" {4} Speical Non-Working Day and Rest Day")
    println(" {5} Regular Holiday")
    println(" {6} Regular Holiday and Rest Day")
    println("------------------------------------------")
    println("${day}")
}

//modifies the day type based on day selected
fun modifyType(dayOfWeek: String, config: Settings, 
               day: Map<Int, String>, show: Int, nDay: Int) {
    var nTemp: Int
    if (show == 1)
        printTypeDay("${dayOfWeek}: ${config.typeDay[day[nDay]]}")
    else println("${dayOfWeek}: ${config.typeDay[day[nDay]]}")   

    do {
        print("    CHANGE TO ")
        nTemp = Integer.valueOf(readLine())
        when (nTemp) {
            1 -> config.specificTypeDay(dayOfWeek, "Normal Day")
            2 -> config.specificTypeDay(dayOfWeek, "Rest Day")
            3 -> config.specificTypeDay(dayOfWeek, "Special Non-Working Day")
            4 -> config.specificTypeDay(dayOfWeek, "Special Non-Working Day and Rest Day")
            5 -> config.specificTypeDay(dayOfWeek, "Regular Holiday")
            6 -> config.specificTypeDay(dayOfWeek, "Regular Holiday and Rest Day")
            else -> println("   WARNING: Please input 1-6 only!\n")
        }
    } while (nTemp < 1 || nTemp > 6)
    println(">>NEW ${dayOfWeek} -- ${config.typeDay[day[nDay]]}\n")
}

//modifies the in time based on day selected
fun modifyIn(dayOfWeek: String, config: Settings, 
             day: Map<Int, String>, nDay: Int) {
    var nTemp: Int

    do {
        print("${dayOfWeek}: ${config.inDay[day[nDay]]} CHANGE TO ") 
        nTemp = Integer.valueOf(readLine())
        //check if valid time
        if (!isValidTime(nTemp))
            println("ERROR: Invalid time! Input again (0000 - 2359).\n")
    } while (!isValidTime(nTemp))

    config.specificInDay(dayOfWeek, String.format("%04d", nTemp))
    println(">>NEW ${dayOfWeek} -- ${config.inDay[day[nDay]]}\n")
}

//modifies the out time based on day selected
fun modifyOut(dayOfWeek: String, config: Settings, 
             day: Map<Int, String>, nDay: Int) {
    var nTemp: Int

    do {
        print("${dayOfWeek}: ${config.outDay[day[nDay]]} CHANGE TO ")  
        nTemp = Integer.valueOf(readLine())
        //check if valid time
        if (!isValidTime(nTemp))
            println("ERROR: Invalid time! Input again (0000 - 2359).\n")
    } while (!isValidTime(nTemp))

    config.specificOutDay(dayOfWeek, String.format("%04d", nTemp))
    println(">>NEW ${dayOfWeek} -- ${config.outDay[day[nDay]]}\n")
}

//OPTION 2 Function
fun modify(config: Settings, day: Map<Int, String>) {
    var dTemp: Double = 0.00
    var nTemp: Int
    var nOpt: Int
    var ans: Int
    
    printSettings(config, day, "Default Settings")

    do {
        println("MODIFY OPTIONS MENU -- Employee ${config.employeeNumber}")
        println("   (1) Change Daily Salary")
        println("   (2) Change Maximum Regular Hours")
        println("   (3) Change Work Days (and Rest Days)")
        println("   (4) Type of day")
        println("   (5) In Time for each day")
        println("   (6) Out Time for each day")
        println("   (7) Exit Settings")
        print("What would you like to modify? ")
        nOpt = Integer.valueOf(readLine())

        when (nOpt) {
            1 -> {
                do {
                    print("Daily Salary: ${config.salary} CHANGE TO ")
                    try {
                        var tempNull = readLine()?.toDouble()
                        if(tempNull == null)
                            dTemp = 0.00
                        else dTemp = tempNull
                        if (dTemp <= 0)
                            println("ERROR: Invalid salary. The value should be more than 0.")
                    } catch (e: NumberFormatException) {
                        println("ERROR: Invalid number format.")
                    }
                } while (dTemp <= 0)
                config.newSalary(dTemp) //new salary
                println(">>NEW Daily Salary: ${config.salary}\n")
            }
            2 -> {
                do {
                    print("Maximum Regular Hours: ${config.maxHours} CHANGE TO ")
                    nTemp = Integer.valueOf(readLine())
                    if (nTemp <= 0)
                        println("ERROR: Invalid max hours. The value should be more than 0.")
                    else if (nTemp >= 24)
                        println("ERROR: Invalid max hours. Please no.")
                } while (nTemp <= 0 || nTemp >= 24)
                config.newMaxHours(nTemp) //new hours
                println(">>NEW Max Regular Hours: ${config.maxHours}\n")
            }
            3 -> {
                do {
                    print("Work Days: ${config.workDay} CHANGE TO ")
                    nTemp = Integer.valueOf(readLine())
                    if (nTemp <= 0)
                        println("ERROR: Invalid work days. The value should be more than 0.")
                    else if (nTemp > 7)
                        println("ERROR: Invalid work days. There are only 7 days in a week.")
                } while (nTemp <= 0 || nTemp > 7)
                config.newWorkDay(nTemp) //new hours
                println(">>NEW Work Days: ${config.workDay}")
                println(">>NEW Rest Days: ${config.restDay}\n")
            }
            4 -> {
                printOptDays()
                do {
                    print("Which would you like to change? ")
                    ans = Integer.valueOf(readLine())
                    when (ans) {
                        1 -> modifyType("Monday", config, day, 1, 1)
                        2 -> modifyType("Tuesday", config, day, 1, 2)
                        3 -> modifyType("Wednesday", config, day, 1, 3)
                        4 -> modifyType("Thursday", config, day, 1, 4)
                        5 -> modifyType("Friday", config, day, 1, 5)
                        6 -> modifyType("Saturday", config, day, 1, 6)
                        7 -> modifyType("Sunday", config, day, 1, 7)
                        8 -> {
                            modifyType("Monday", config, day, 1, 1)
                            modifyType("Tuesday", config, day, 0, 2)
                            modifyType("Wednesday", config, day, 0, 3)
                            modifyType("Thursday", config, day, 0, 4)
                            modifyType("Friday", config, day, 0, 5)
                            modifyType("Saturday", config, day, 0, 6)
                            modifyType("Sunday", config, day, 0, 7)
                        } 
                        9 -> {
                            println("Going back to Modifying Settings...\n\n")
                        }
                        else -> println("   WARNING: Kindly input numbers 1 to 9 only!\n")
                    }
                } while (ans < 1 || ans > 9)
            }
            5 -> {
                printOptDays()
                do {
                    print("Which would you like to change? ")
                    ans = Integer.valueOf(readLine())
                    when (ans) {
                        1 -> modifyIn("Monday", config, day, 1)
                        2 -> modifyIn("Tuesday", config, day, 2)
                        3 -> modifyIn("Wednesday", config, day, 3)
                        4 -> modifyIn("Thursday", config, day, 4)
                        5 -> modifyIn("Friday", config, day, 5)
                        6 -> modifyIn("Saturday", config, day, 6)
                        7 -> modifyIn("Sunday", config, day, 7)
                        8 -> {
                            modifyIn("Monday", config, day, 1)
                            modifyIn("Tuesday", config, day, 2)
                            modifyIn("Wednesday", config, day, 3)
                            modifyIn("Thursday", config, day, 4)
                            modifyIn("Friday", config, day, 5)
                            modifyIn("Saturday", config, day, 6)
                            modifyIn("Sunday", config, day, 7)
                        } 
                        9 -> {
                            println("Going back to Modifying Settings...\n\n")
                        }
                        else -> println("   WARNING: Kindly input numbers 1 to 9 only!\n")
                    }
                } while (ans < 1 || ans > 9)
            }
            6 -> {
                printOptDays()
                do {
                    print("Which would you like to change? ")
                    ans = Integer.valueOf(readLine())
                    when (ans) {
                        1 -> modifyOut("Monday", config, day, 1)
                        2 -> modifyOut("Tuesday", config, day, 2)
                        3 -> modifyOut("Wednesday", config, day, 3)
                        4 -> modifyOut("Thursday", config, day, 4)
                        5 -> modifyOut("Friday", config, day, 5)
                        6 -> modifyOut("Saturday", config, day, 6)
                        7 -> modifyOut("Sunday", config, day, 7)
                        8 -> {
                            modifyOut("Monday", config, day, 1)
                            modifyOut("Tuesday", config, day, 2)
                            modifyOut("Wednesday", config, day, 3)
                            modifyOut("Thursday", config, day, 4)
                            modifyOut("Friday", config, day, 5)
                            modifyOut("Saturday", config, day, 6)
                            modifyOut("Sunday", config, day, 7)
                        } 
                        9 -> {
                            println("Going back to Modifying Settings...\n\n")
                        }
                        else -> println("   WARNING: Kindly input numbers 1 to 9 only!\n")
                    }
                } while (ans < 1 || ans > 9)
            }
            7 -> {  
                printSettings(config, day, "  New Settings")
                println("\nGoing back to Main Menu...")
            }
            else -> println("   WARNING: Kindly input numbers 1 to 7 only!\n")
        }
    } while (nOpt != 7)
}

fun main() {
    var nOpt: Int
    var employees = ArrayList<Settings>()
    var oneEmp = false
    var count: Int
    var wrongEmp: Boolean

    //for reference
    var getDay = mapOf(0 to "Error", 1 to "Monday", 2 to "Tuesday", 3 to "Wednesday",
        4 to "Thursday", 5 to "Friday", 6 to "Saturday", 7 to "Sunday", 8 to "Error")

    do {
        var empNo: Int

        println("\n\nWelcome to the Weekly Payroll System!")
        println("   [1] Generate the Payroll")
        println("   [2] Modify Configuration")
        println("   [3] Add Employee")
        println("   [4] Exit Program")
        print("What would you like to do? ")
        nOpt = Integer.valueOf(readLine())
        
        when (nOpt) {
            1, 2 -> {
                if (!oneEmp)
                    println("ERROR: No Existing Employees. Add one!")
                else {
                    do {
                        wrongEmp = true
                        count = 0
                        print("Enter employee ID: ")
                        empNo = Integer.valueOf(readLine())
                        for (i in employees) {
                            if (empNo == i.employeeNumber) {
                                wrongEmp = false
                                break;
                            }
                            count++
                        }
                        if (wrongEmp)
                            println("ERROR: Employee ${empNo} does not exist!")
                    } while (wrongEmp)

                    var config = employees.get(count)
                    if (nOpt == 1)
                        generatePayroll(config, getDay)
                    else //(nOpt == 2)
                        modify(config, getDay)
                }
            }
            3 -> {
                do {
                    wrongEmp = true
                    print("Add Employee ID: ")
                    empNo = Integer.valueOf(readLine())
                    for (i in employees)
                        if (empNo == i.employeeNumber)
                            wrongEmp = false
                    if (!wrongEmp)
                        println("ERROR: Employee ${empNo} already exist!")
                } while (!wrongEmp)
                var typeDay = mutableMapOf("Monday"     to "Normal Day", 
               "Tuesday"  to "Normal Day", "Wednesday"  to "Normal Day",
               "Thursday" to "Normal Day", "Friday"     to "Normal Day",
               "Saturday" to "Rest Day",   "Sunday"     to "Rest Day")
                var inDay = mutableMapOf  ("Monday"     to "0900",
                 "Tuesday"    to "0900",   "Wednesday"  to "0900",
                 "Thursday"   to "0900",   "Friday"     to "0900",
                 "Saturday"   to "0900",   "Sunday"     to "0900")
                var outDay = mutableMapOf ("Monday"     to "0900", 
                 "Tuesday"    to "0900",   "Wednesday"  to "0900",
                 "Thursday"   to "0900",   "Friday"     to "0900",
                 "Saturday"   to "0900",   "Sunday"     to "0900")
                employees.add(Settings(empNo, 500.00, 8, 5, 2, typeDay, inDay, outDay))
                oneEmp = true
                println("ADDED SUCCESSFULLY!")
            }
            4 -> println("\nExiting...")
            else -> println("   WARNING: Kindly input 1, 2, 3, or 4 only!")
        }
    } while (nOpt != 4)
   
    println("Thank you for using the application!")
}

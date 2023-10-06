package main

import (
	"container/list"
	"fmt"
	"math"
	"math/rand"
	"strconv"
	"sync"
	"time"
)

var adjTowns [][]float64

type VertexTown struct {
	name string
}

var towns []string

var deletedTowns *list.List
var DFSVertexList *list.List

var noWayToStop sync.WaitGroup

var rwl sync.RWMutex

func changeCost() {
	for {
		rwl.Lock()
		fmt.Println()
		var i, j int
		for {
			i = rand.Intn(len(adjTowns))
			j = rand.Intn(len(adjTowns))
			if adjTowns[i][j] != 0 && i != j {
				break
			}
		}
		adjTowns[i][j] = float64(rand.Intn(100000)) / 100
		adjTowns[j][i] = adjTowns[i][j]
		fmt.Println("Змінено вартість квитка від", towns[i],
			"до", towns[j], ", тепер вона складає", math.Trunc(adjTowns[i][j]), "гривень",
			int((adjTowns[i][j]-math.Trunc(adjTowns[i][j]))*100), "копійок.")
		rwl.Unlock()
		time.Sleep(time.Duration(rand.Intn(20)) * time.Second)
	}
}

func onlyOneAction() int {
	var anyRouteExists, allRoutesExist bool
	anyRouteExists = false
	allRoutesExist = true
	for i := range adjTowns {
		for j := i + 1; j < len(adjTowns[i]); j++ {
			if adjTowns[i][j] > 0 {
				anyRouteExists = true
			}
			if adjTowns[i][j] == 0 {
				allRoutesExist = false
			}
		}
		if !allRoutesExist && anyRouteExists {
			return 1 //Можна і видаляти, і додавати
		}
	}
	if !anyRouteExists {
		return 0 //Нема жодного маршруту, видаляти нема звідки
	}
	return 2 //Існують усі прямі маршрути, додавати нема чого
}

func addOrRemoveRoutes() {
	for {
		rwl.Lock()
		fmt.Println()
		var i, j, action int
		ooa := onlyOneAction()
		if ooa == 0 {
			action = 0
		} else if ooa == 1 {
			action = rand.Intn(2)
		} else {
			action = 1
		}
		switch action {
		case 0: //Додати
			for {
				i = rand.Intn(len(adjTowns))
				j = rand.Intn(len(adjTowns))
				if adjTowns[i][j] == 0 && i != j {
					break
				}
			}
			adjTowns[i][j] = float64(rand.Intn(100000)) / 100
			adjTowns[j][i] = adjTowns[i][j]
			fmt.Println("Додано рейс від", towns[i], "до", towns[j], ", вартість квитка складає",
				math.Trunc(adjTowns[i][j]), "гривень",
				int((adjTowns[i][j]-math.Trunc(adjTowns[i][j]))*100), "копійок.")
		case 1: //Видалити
			for {
				i = rand.Intn(len(adjTowns))
				j = rand.Intn(len(adjTowns))
				if adjTowns[i][j] != 0 && i != j {
					break
				}
			}
			adjTowns[i][j] = 0.
			adjTowns[j][i] = adjTowns[i][j]
			fmt.Println("Видалено рейс від", towns[i], "до", towns[j])
		}
		rwl.Unlock()
		time.Sleep(time.Duration(rand.Intn(20)) * time.Second)
	}
}

func addOrRemoveTowns() {
	for {
		rwl.Lock()
		fmt.Println()
		var action int
		if len(towns) == 0 {
			action = 0
		} else if len(towns) == 50 {
			action = 1
		} else {
			action = rand.Intn(2)
		}
		switch action {
		case 0: //Додати
			newAdjTowns := make([][]float64, len(adjTowns)+1)
			for i := range newAdjTowns {
				newAdjTowns[i] = make([]float64, len(newAdjTowns))
			}
			for i := range adjTowns {
				for j := range adjTowns[i] {
					newAdjTowns[i][j] = adjTowns[i][j]
				}
			}
			for i := range newAdjTowns {
				if i == len(adjTowns) {
					newAdjTowns[i][i] = 0.
				}
				routeShouldExist := rand.Intn(2)
				switch routeShouldExist {
				case 0:
					newAdjTowns[i][len(adjTowns)] = 0.
				case 1:
					newAdjTowns[i][len(adjTowns)] = float64(rand.Intn(100000)) / 100
				}
				newAdjTowns[len(adjTowns)][i] = newAdjTowns[i][len(adjTowns)]
			}
			adjTowns = newAdjTowns
			if deletedTowns.Front() == nil {
				towns = append(towns, importNames()[len(towns)])
			} else {
				towns = append(towns, importNames()[deletedTowns.Front().Value.(int)])
				deletedTowns.Remove(deletedTowns.Front())
			}
			fmt.Println("Додано місто", towns[len(towns)-1])

		case 1: //Видалити
			indToDelete := rand.Intn(len(adjTowns))
			townToDelete := towns[indToDelete]
			newAdjTowns := make([][]float64, len(adjTowns)-1)
			newTowns := make([]string, len(towns)-1)
			for i := range newAdjTowns {
				newAdjTowns[i] = make([]float64, len(newAdjTowns))
			}
			for i := range adjTowns {
				if i == indToDelete {
					continue
				}
				switch {
				case i < indToDelete:
					newTowns[i] = towns[i]
				case i > indToDelete:
					newTowns[i-1] = towns[i]
				}
				for j := range adjTowns[i] {
					if j == indToDelete {
						continue
					}
					switch {
					case i < indToDelete && j < indToDelete:
						newAdjTowns[i][j] = adjTowns[i][j]
					case i < indToDelete && j > indToDelete:
						newAdjTowns[i][j-1] = adjTowns[i][j]
					case i > indToDelete && j < indToDelete:
						newAdjTowns[i-1][j] = adjTowns[i][j]
					case i > indToDelete && j > indToDelete:
						newAdjTowns[i-1][j-1] = adjTowns[i][j]
					}
				}
			}
			adjTowns = newAdjTowns
			deletedTowns.PushBack(findInNames(townToDelete))
			towns = newTowns
			fmt.Println("Видалено місто", townToDelete)
		}
		rwl.Unlock()
		time.Sleep(time.Duration(rand.Intn(30)) * time.Second)
	}
}

func routeExistsDFS(cur int, end int, visited []bool) bool {
	DFSVertexList.PushBack(cur)
	if cur == end {
		return true
	}
	visited[cur] = true
	for i := range adjTowns {
		if adjTowns[cur][i] > 0 && (!visited[i]) {
			if routeExistsDFS(i, end, visited) {
				return true
			}
		}
	}
	if DFSVertexList.Back().Value.(int) == cur {
		DFSVertexList.Remove(DFSVertexList.Back())
	}
	return false
}

func getCost(thelist *list.List) float64 {
	sum := 0.
	start := thelist.Front()
	prev := start.Value.(int)
	for e := start.Next(); e != nil; e = e.Next() {
		sum += adjTowns[prev][e.Value.(int)]
		prev = e.Value.(int)
	}
	return sum
}

func routeAtoB() {
	for {
		rwl.RLock()
		var i, j int
		for {
			i = rand.Intn(len(adjTowns))
			j = rand.Intn(len(adjTowns))
			if i != j {
				break
			}
		}
		visited := make([]bool, len(adjTowns))
		DFSVertexList = list.New()
		if adjTowns[i][j] == 0 {
			routeExistsDFS(i, j, visited)
		} else {
			DFSVertexList.PushBack(i)
			DFSVertexList.PushBack(j)
		}
		fmt.Println()
		if DFSVertexList.Front() != nil {
			switch {
			case adjTowns[i][j] > 0:
				fmt.Println("Існує прямий рейс від міста " + towns[i] /*.name*/ + " до міста " + towns[j] /*.name*/)
				cost := adjTowns[i][j]
				fmt.Println("Його вартість становить ", math.Trunc(cost), " гривень ",
					int((cost-math.Trunc(cost))*100), " копійок.")
			default:
				fmt.Println("Існує шлях із пересадками між рейсами від міста " + towns[i] /*.name*/ + " до міста " + towns[j] /*.name*/)
				cost := getCost(DFSVertexList)
				fmt.Println("Його вартість становить ", math.Trunc(cost), " гривень ",
					int((cost-math.Trunc(cost))*100), " копійок.")
			}
		} else {
			fmt.Println("Немає прямого рейсу від міста ", towns[i] /*.name*/, " до міста ", towns[j] /*.name*/)
		}
		rwl.RUnlock()
		time.Sleep(5 * time.Second)
	}
}

func main() {
	townNames := importNames()
	fmt.Print("Введіть кількість міст: ")
	var amts string
	fmt.Scan(&amts)
	amt, _ := strconv.Atoi(amts)
	adjTowns = make([][]float64, amt)
	towns = make([]string, amt)
	deletedTowns = list.New()
	for i := range adjTowns {
		adjTowns[i] = make([]float64, amt)
		towns[i] = townNames[i]
	}
	for i := range adjTowns {
		adjTowns[i][i] = 0
		for j := i + 1; j < amt; j++ {
			routeShouldExist := rand.Intn(2)
			switch routeShouldExist {
			case 0:
				adjTowns[i][j] = 0
			case 1:
				adjTowns[i][j] = float64(rand.Intn(100000)) / 100
			}
			adjTowns[j][i] = adjTowns[i][j]
		}
	}

	noWayToStop.Add(1)
	go routeAtoB()
	go changeCost()
	go addOrRemoveRoutes()
	go addOrRemoveTowns()
	noWayToStop.Wait()
}

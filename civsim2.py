from colorsys import hsv_to_rgb
from random import random
from Tkinter import *
from time import sleep
from thread import start_new_thread
import math

INITIAL_LOYALTY=20
MORE_CIV=0
MORE_SOL=1
FOUND_CITY=2
STARVING=3
ATTACK=4
LOYALTY=0
DISTANCE=1
LOYALTY_BASED=2
class City:
	def __init__(self,x,y,nation):
		self.nation=nation
		self.ix=x
		self.iy=y
		p=getRealPos([x,y])
		self.rx=p[0]
		self.ry=p[1]
		self.fertility=land[x][y]
		canvas.itemconfig(getTag(x,y),fill=getColor(nation))
		land[x][y]=self
		e=getEmperor(nation)
		e.cities+=1
		e.focus+=1
		self.soldiers=10
		self.civilians=20
		self.food=0
		self.loyalty=INITIAL_LOYALTY
def forEachCity():
	for x in range(dimension):
		for y in range(dimension):
			c=land[x][y]
			if isinstance(c,City):
				if rand(0,1)==0:				#2?
					c.civilians-=4
					if c.civilians<0:
						c.civilians=0
					c.soldiers-=4
					if c.soldiers<0:
						c.soldiers=0
				if not secedeAction(c):
					foodRequests(c)
					surroundingsRequests(c)
def secedeAction(c):
	if c.loyalty<=0:
		e=getEmperor(c.nation)
		if e.ix==c.ix and e.iy==c.iy:
			periodOfWarringStates(e,None,c)
		else:
			secede(c)
		return True
	return False
def foodRequests(c):
	f=c.fertility
	f=(f*c.civilians)-(c.civilians+c.soldiers)
	if f<=0:
		Request(MORE_CIV,c)
		c.loyalty-=1
	c.food+=f
	if c.food<0:
		c.soldiers+=c.food
		c.food=0
		if c.soldiers<0:
			c.civilians+=c.soldiers
			c.soldiers=0
			if c.civilians<0:
				c.civilians=0
		Request(STARVING,c)
		c.loyalty-=1
def surroundingsRequests(c):
	s=getSurroundings(c)
	city=False
	attack=False
	friendlyCityNearby=False
	e=getEmperor(c.nation)
	foundCity=None
	for a in range(len(s)):
		thing=s[a]
		if isinstance(thing,City):
			if not thing.nation==c.nation:
				e1=getEmperor(thing.nation)
				diff=abs(e.cities-e1.cities)
				if diff<20 and e.age>5 and e1.age>5:
					if rand(0,2)==0:
						if e1.cities>100 and rand(0,3)==0:
							periodOfWarringStates(e1,e,thing)
						else:
							merge(e,e1)
					else:
						if e.cities>100 and rand(0,3)==0:
							periodOfWarringStates(e,e1,c)
						else:
							merge(e1,e)
					return
				elif not attack:
					if thing.soldiers>=c.soldiers:
						Request(MORE_SOL,c)
						c.loyalty-=1
					else:
						Request(ATTACK,c).target=[thing.ix,thing.iy]
					attack=True
			else:
				friendlyCityNearby=True
		elif not city:
			foundCity=Request(FOUND_CITY,c)
			foundCity.target=thing
			city=True
	if not friendlyCityNearby and t>4:
		collapse(c)
		canvas.itemconfig(getTag(c.ix,c.iy),fill=getFertilityColor(c.fertility))
		if not foundCity==None:
			e.requests.remove(foundCity)
def collapse(city):
	land[city.ix][city.iy]=city.fertility
	e=getEmperor(city.nation)
	e.cities-=1
	if city.ix==e.ix and city.iy==e.iy and e.cities>0:
		ecs=[]
		for x in range(dimension):
			for y in range(dimension):
				c=land[x][y]
				if isinstance(c,City) and c.nation==e.nation:
					ecs.append(c)
		emp=Emperor(city=ecs[int(rand(0,len(ecs)))],index=emperors.index(e))
		for a in range(0,len(ecs)):
			changeNation(ecs[a],emp.nation)
	updateCanvas()
def getSurroundings(city):
	s=[]
	for x in range(city.ix-1,city.ix+2):
		for y in range(city.iy-1,city.iy+2):
			if not (x==city.ix and y==city.iy):
				if distance([city.ix,city.iy],[x,y])<2*side:
					if x>=0 and y>=0 and x<dimension and y<dimension:
						if isinstance(land[x][y],City):
							s.insert(int(rand(0,len(s)+1)),land[x][y])
						elif land[x][y]>0:
							s.insert(int(rand(0,len(s)+1)),[x,y])
						else:
							delta=[x-city.ix,y-city.iy]
							coor=[x,y]
							edge=False
							swim=4
							while swim>0 and land[coor[0]][coor[1]]==0:
								coor[0]+=delta[0]
								coor[1]+=delta[1]
								swim-=1
								if coor[0]<0 or coor[0]>=dimension or coor[1]<0 or coor[1]>=dimension:
									edge=True
									break
							if edge==False:
								if land[coor[0]][coor[1]]>0:
									s.insert(int(rand(0,len(s)+1)),[coor[0],coor[1]])
	return s
def forEachEmperor():
	for a in range(len(emperors)-1,-1,-1):
		e=emperors[a]
		e.age+=1
		if e.cities>0:
			s=0
			for b in range(e.focus+s):
				if b>=len(e.requests):
					break
				if not e.requests[b].city.nation==e.nation or not appeaseRequest(e.requests[b]):
					s+=1
			e.requests=[]
			if e.cities<=e.lastCities:
				setParameters(e)
			e.lastCities=e.cities
def appeaseRequest(r):
	if r.type==STARVING:
		r.city.food+=20
		r.city.loyalty+=rand(-1,4)
	elif r.type==MORE_CIV:
		r.city.civilians+=2
		r.city.loyalty+=rand(-1,4)
	elif r.type==MORE_SOL:
		r.city.soldiers+=2
		r.city.loyalty+=rand(-1,4)
	elif r.type==FOUND_CITY:
		if isinstance(land[r.target[0]][r.target[1]],City):
			return False
		if land[r.target[0]][r.target[1]]==0:
			return False
		City(r.target[0],r.target[1],r.city.nation)
	elif r.type==ATTACK:
		c=land[r.target[0]][r.target[1]]
		if isinstance(c,City) and c.soldiers<r.city.soldiers:
			r.city.soldiers-=int(c.soldiers/2)
			c.soldiers=int(c.soldiers/2)
			e=getEmperor(c.nation)
			if e.ix==c.ix and e.iy==c.iy:
				if rand(0,10)==0:
					merge(getEmperor(r.city.nation),e)
				else:
					periodOfWarringStates(e,getEmperor(r.city.nation),c)
			else:
				changeNation(c,r.city.nation)
		else:
			return False
	return True
def changeNation(c,n):
	getEmperor(c.nation).cities-=1
	c.nation=n
	canvas.itemconfig(getTag(c.ix,c.iy),fill=getColor(n))
	c.loyalty=INITIAL_LOYALTY
	if c.soldiers<10:
		c.soldiers=10
	if c.civilians<10:
		c.civilians=10
	if c.food==0:
		c.food=100
	e=getEmperor(n)
	e.cities+=1
	if rand(0,2)==0:
		e.focus+=1
def merge(gains,merges):
	for x in range(dimension):
		for y in range(dimension):
			c=land[x][y]
			if isinstance(c,City) and c.nation==merges.nation:
				changeNation(c,gains.nation)
			if merges.cities==0:
				break
		if merges.cities==0:
			break
	updateCanvas()
def secede(r):
	e=getEmperor(r.nation)
	if r.ix==e.ix and r.iy==e.iy:
		setParameters(e)
		changeNation(r,r.nation)
		return
	Emperor(city=r,index=emperors.index(e))
	canvas.update_idletasks()
	ecs=[]
	for x in range(dimension):
		for y in range(dimension):
			c=land[x][y]
			if isinstance(c,City) and c.nation==e.nation and distance([r.rx,r.ry],[c.rx,c.ry])<500:
				if e.ix==c.ix and e.iy==c.iy:
					periodOfWarringStates(e,getEmperor(r.nation),c)
					return
				ecs.append(c)
	for a in ecs:
		changeNation(a,r.nation)
		canvas.update_idletasks()
	#hisCan.graphPopulation()
	updateCanvas()
def periodOfWarringStates(e,conqueror,city):
	ecs=[]
	for x in range(dimension):
		for y in range(dimension):
			c=land[x][y]
			if isinstance(c,City) and c.nation==e.nation and not (c.ix==city.ix and c.iy==city.iy):
				ecs.insert(int(rand(0,len(ecs))),c)
			if len(ecs)==e.cities-1:
				break
	ecs.insert(0,city)
	n=int(len(ecs)/35)
	if n==0:
		n=1
	capitals=[None for a in range(n)]
	i=emperors.index(e)
	for a in range(n):
		cap=ecs[a]
		if a==0 and not conqueror==None:
			changeNation(cap,conqueror.nation)
		else:
			if e.ix==cap.ix and e.iy==cap.iy:
				setParameters(e)
				changeNation(cap,cap.nation)
			else:
				Emperor(city=cap,index=i)
		capitals[a]=[cap.ix,cap.iy]
	for a in range(n,len(ecs)):
		c=ecs[a]
		nation=ecs[int(closest([c.ix,c.iy],capitals)[0])].nation
		changeNation(c,nation)
	updateCanvas()
nextNation=0
class Emperor():
	def __init__(self,**params):
		global nextNation
		self.nation=nextNation
		nextNation+=5
		self.cities=0
		self.lastCities=0
		self.focus=50
		self.age=0
		if "index" in params:
			emperors.insert(params["index"],self)
		else:
			emperors.append(self)
		setParameters(self)
		self.requests=[]
		if "city" in params:
			c=params["city"]
			self.ix=c.ix
			self.iy=c.iy
			changeNation(c,self.nation)
		else:
			self.ix=params["x"]
			self.iy=params["y"]
			City(self.ix,self.iy,self.nation)
		self.rx=land[self.ix][self.iy].rx
		self.ry=land[self.ix][self.iy].ry
		r=yDis
		self.dot=canvas.create_oval(scaled(self.rx-r,scroll[0]),scaled(self.ry-r,scroll[1]),scaled(self.rx+r,scroll[0]),scaled(self.ry+r,scroll[1]),fill="black")
def scaled(num,scroll):
	return (num*scale)+scroll
def setParameters(e):
	e.requestTypes=[None for a in range(5)]
	for a in range(len(e.requestTypes)):
		e.requestTypes[a]=float(rand(-50,51))/float(10)
	e.coefficients=[None for a in range(3)]
	for a in range(len(e.coefficients)):
		e.coefficients[a]=float(rand(-20,21))/float(10)
class Request():
	def __init__(self,type,city):
		self.city=city
		self.type=type
		e=getEmperor(city.nation)
		p=e.requestTypes[type]
		p+=e.coefficients[LOYALTY]*city.loyalty
		p+=e.coefficients[DISTANCE]*distance([city.rx,city.ry],[e.rx,e.ry])
		if type==MORE_CIV or type==MORE_SOL or type==STARVING:
			p+=e.coefficients[LOYALTY_BASED]
		else:
			p-=e.coefficients[LOYALTY_BASED]
		self.priority=round(p,2)
		a=0
		for r in e.requests:
			if r.priority<self.priority:
				e.requests.insert(a,self)
				return
			a+=1
		e.requests.append(self)
def updateCanvas():
	for a in range(len(emperors)):
		e=emperors[a]
		if e.cities==0:
			canvas.delete(e.dot)
	hisCan.graphPopulation()
	canvas.update_idletasks()
	root.update()
def getTag(x,y):
	return str(x)+","+str(y)
def rand(min,max):
	return math.floor(random()*(max-min))+min
def distance(one,two):
	if isinstance(one,City) and isinstance(two,City):
		return math.sqrt(((one.rx-two.rx)**2)+((one.ry-two.ry)**2))
	one=getRealPos(one)
	two=getRealPos(two)
	return math.sqrt(((one[0]-two[0])**2)+((one[1]-two[1])**2))
def getRealPos(p):
	return [(p[0]+(0.5*(p[1]%2)))*side*sq3,1.5*side*p[1]]
def closest(point,points):
	dis=distance(point,points[0])
	index=0
	i=1
	for i in range(len(points)-1):
		d=distance(point,points[i+1])
		if d<dis:
			dis=d
			index=i+1
	return [index,dis]
def drawHexagon(canvas,origin,color):
	origin=getRealPos(origin)
	x=origin[0]
	y=origin[1]
	if color=="blue":
		return canvas.create_polygon(x,y-side,x+xDis,y-yDis,x+xDis,y+yDis,x,y+side,x-xDis,y+yDis,x-xDis,y-yDis,fill=color,width=0)
	else:
		return canvas.create_polygon(x,y-side,x+xDis,y-yDis,x+xDis,y+yDis,x,y+side,x-xDis,y+yDis,x-xDis,y-yDis,fill=color,outline="black")
def spawnContinent():
	for s in range(50):
		x=int(rand(0,dimension))
		y=int(rand(0,dimension))
		f=1
		if rand(0,4)==0:
			f=-1;
		seeds.append([x,y,f])
		land[x][y]=f
		if f==1:
			g=drawHexagon(canvas,[x,y],getFertilityColor(land[x][y]))
			canvas.itemconfig(g,tag=getTag(x,y))
	for x in range(dimension):
		for y in range(dimension):
			if land[x][y]==0:
				seed=closest([x,y],seeds)
				land[x][y]=seeds[seed[0]][2]
				if land[x][y]==1:
					land[x][y]=math.floor(seed[1])*2
					g=drawHexagon(canvas,[x,y],getFertilityColor(land[x][y]))
					canvas.itemconfig(g,tag=getTag(x,y))
					if rand(0,100)==0:
						Emperor(x=x,y=y)
			if land[x][y]==-1:
				land[x][y]=0
				drawHexagon(canvas,[x,y],"blue")
def getColor(nation):
		e=getEmperor(nation)
		pos=getRealPos([e.ix,e.iy])
		if nation%3==0:
			r=(nation*e.ix*10)%255
			g=(((e.iy*20)+(e.ix*5))+255-(nation*20))%155
			while g<0 :
				g+=255
			b=(((nation/60)+100)*(e.iy+5)*6)%255
		elif nation%2==0:
			r=(pos[0]+pos[1])%255
			g=(pos[1]-(2*nation)+e.ix)%255
			while g<0:
				g+=255
			b=((e.ix+nation)*e.iy)%255
		else:
			r=(300-(nation*(pos[0]-pos[1])))%255
			while r<0:
				r+=255
			g=((pos[0]+pos[1])/2)%255
			b=(e.ix*e.iy*nation)%255
		return "#%02x%02x%02x" % (r,g,b)
def getFertilityColor(f):
	h=0.333
	s=0.2+(f/150)
	if s>1:
		s=1
	v=0.75
	rgb=hsv_to_rgb(h,s,v)
	rgb=list(rgb)
	for a in range(len(rgb)):
		rgb[a]=int(round(rgb[a],2)*256)
		if rgb[a]==256:
			rgb[a]-=1
	rgb=tuple(rgb)
	return "#%02x%02x%02x" % rgb
def getEmperor(nation):
	for e in emperors:
		if e.nation==nation:
			return e
def scrollCanvas(x,y):
	global scroll
	if scroll[0]+x>0:
		x=-scroll[0]
	if scroll[0]+(cWidth*(scale-1))+x<0:
		x=-(scroll[0]+(cWidth*(scale-1)))
	if scroll[1]+y>0:
		y=-scroll[1]
	if scroll[1]+(cHeight*(scale-1))+y<0:
		y=-(scroll[1]+(cHeight*(scale-1)))
	if not (x==0 and y==0):
		canvas.move(ALL,x,y)
		scroll[0]+=x
		scroll[1]+=y
def scaleCanvas(event):
	global scale
	if scale==1:
		s=2
	else:
		s=1
		scrollCanvas(-scroll[0],-scroll[1])
	factor=float(s)/float(scale)
	canvas.scale(ALL,0,0,factor,factor)
	scale=s
def mouseDown(event):
	global dragPos
	dragPos=[event.x,event.y]
def mouseMove(event):
	global dragPos
	scrollCanvas(event.x-dragPos[0],event.y-dragPos[1])
	dragPos=[event.x,event.y]
def mouseUp(event):
	global dragPos
	dragPos=[0,0]
class SimulationCanvas(Canvas):
	def __init__(self,root):
		Canvas.__init__(self,root,width=cWidth,height=cHeight,highlightthickness=0)
		self.pack()
		self.bind("<ButtonPress-1>",mouseDown)
		self.bind("<ButtonRelease-1>",mouseUp)
		self.bind("<B1-Motion>",mouseMove)
		self.bind("<Double-1>",scaleCanvas)
class HistogramCanvas(Canvas):
	def __init__(self,root):
		Canvas.__init__(self,root,width=cWidth,height=100,highlightthickness=0,bg="black")
		self.pack()
		self.x=0
		self.highest=100
		self.factor=[1.0,1.0]
	def graphPopulation(self):
		bottom=100.0
		self.scale(ALL,0,100,1/self.factor[0],1/self.factor[1])
		for e in emperors:
			r=self.create_rectangle(self.x,bottom-e.cities,self.x+1,bottom,width=0,fill=getColor(e.nation))
			bottom-=e.cities
		self.x+=1.0
		if bottom<self.highest:
			self.highest=bottom
		scaleX=round(cWidth/self.x,2)
		scaleY=round(100.0/(100-self.highest),2)
		self.scale(ALL,0,100,scaleX,scaleY)
		self.factor=[scaleX,scaleY]
		self.update_idletasks()
		self.update()
dragPos=[0,0]
scroll=[0,0]
scale=1
dimension=35
emperors=[]
land=[[0 for y in range(dimension)] for x in range(dimension)]
seeds=[]
side=8
sq3=1.7321	#simplified square root of 3, use for hexagonal geometry
yDis=side/2
xDis=yDis*sq3
root=Tk()
root.wm_title("CivSim 2.0")
cWidth=2*xDis*(dimension-1)
cHeight=1.5*side*(dimension-1)
canvas=SimulationCanvas(root)
hisCan=HistogramCanvas(root)
spawnContinent()
t=0
while t<60:
	forEachCity()
	forEachEmperor()
	updateCanvas()
	for a in range(len(emperors)-1,-1,-1):
		e=emperors[a]
		if e.cities==0:
			emperors.remove(e)
	sleep(0.5)
	t+=0.5
print("All done!")
root.mainloop()
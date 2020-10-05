import sys

# This list determines the effective fall distance multiplier
# Passive Acrobatics goes in, multiplier on actual fall distance comes out
# Damage is calculated based on EFFECTIVE fall distance, not actual fall distance
# The range is ridiculously wide because of magic items, spells, and Expertise; and so the terrainModTab list can be edited without having to add more indices here
# DC          -21,-16,-11, -6, -1,  4,  9, 14,  19, 24, 29, 34,  39, 44,  49, 54,  59,  64,  69,  74,  79
distModTab = [3.0,2.7,2.4,2.0,1.7,1.3,1.0,0.8,0.65,0.5,0.4,0.3,0.25,0.2,0.15,0.1,0.07,0.05,0.03,0.02,0.01]

# General idea of what kind of terrain each hardness index belongs to
# Water starts at the 5th index, and gets harder per 100' fallen
terrainTypeList = ['Stone', 'Dry ground', 'Soggy ground', 'Marsh', 'Fall broken']

# This list determines how dependent damage is on the terrain, as opposed to the character's Acrobatics
# If you want to universally lower (or raise) damage, just increase (or decrease) every value in the list by the same flat amount
terrainModTab = [0,5,15,25,45]

# Effective max fall distance is terminal velocity distance * highest distance multiplier
maxFall = int(500 * distModTab[0])

# Damage dice table - each index is 10' of distance, so [0] is 0' (i.e. tripping) and [50] is 500'
damageDieTab = [None] * int(maxFall/10 + 1)

# Populate damage dice table
damageDieTab[0] = "1";
damageDieTab[1] = "1d6";
damageDieTab[2] = "2d6";
damageDieTab[3] = "3d8";
damageDieTab[4] = "5d8";
damageDieTab[5] = "7d10";
damageDieTab[6] = "10d10";
damageDieTab[7] = "12d12";
damageDieTab[8] = "14d12";
damageDieTab[9] = "16d12";
# 100' and beyond uses the square root of the distance fallen
for i in range(10,(maxFall//10+1)):
	sqrt = str(round(pow(10*i,0.5)))
	damageDieTab[i] = sqrt+"d20+"+sqrt


def main(fallDist: int, terrainIndex: int, passiveAcro: int) -> None:
	# Final result is passive Acrobatics plus the terrain modifier
	checkResult = passiveAcro + terrainModTab[terrainIndex]

	# Basically for every 5 points you get on the result above, you move one step along the distModTab list
	# The neutral range is 9-13 inclusive - below that, you get penalized. Above, you get rewarded
	distModIndex = (checkResult+1)//5 + 4

	# Bounds on calculated index, just in case
	if distModIndex > 20:
		distModIndex = 20
	elif distModIndex < 0:
		distModIndex = 0
	distMod = distModTab[distModIndex]

	# Terminal velocity reached after falling 500'
	if fallDist > 500:
		fallDist = 500
	fallDist *= distMod

	# Reduce effective distance by 20% for big falls, more survivable for already-lethal campaigns
	if fallDist >= 50:
		fallDist *= 0.8

	# Index in table is 0.1 * the fall distance
	damageDieIndex = int(fallDist/10)

	# Print out that terrain hardness for reference
	print(f"Ground hardness: {terrainTypeList[terrainIndex]}")
	print(f"Damage dice:     {damageDieTab[damageDieIndex]}\n")


if __name__ == '__main__':
	# Can pass fall distance as 1st argument and terrain hardness as 2nd argument, or get prompted here
	if len(sys.argv) < 3 or len(sys.argv) > 4:
		fallDist = int(input('Fall distance: '))
		terrainIndex = int(input('Ground hardness from 1 (stone) to 5 (fall broken): '))
	else:
		fallDist = int(sys.argv[1])
		terrainIndex = int(sys.argv[2])
	# If passing arguments, can pass passive Acrobatics as 3rd argument. Otherwise gets set to 10 by default
	if len(sys.argv) != 4:
		passiveAcro = 10
	else:
		passiveAcro = int(sys.argv[3])

	# Basic input value sanity check
	if fallDist < 0 or terrainIndex < 1 or terrainIndex > 5:
		print("Fall distance must be positive, and ground hardness must be between 1 and 5 inclusive!")
		sys.exit()

	# Terrain hardness, user-facing, is 1 (hard) to 5 (very soft), but the list is 0-indexed, so subtract 1
	main(fallDist, terrainIndex - 1, passiveAcro)

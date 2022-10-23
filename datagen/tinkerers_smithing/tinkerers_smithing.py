import json
import math
import os
import shutil
from copy import deepcopy


def assert_dir(path):
    if not os.path.exists(path):
        os.makedirs(path)


def assert_not_dir(path):
    if os.path.exists(path):
        shutil.rmtree(path)


def write_recipe(recipe_dir, recipe, tier, gear_type, recipe_type):
    with open(recipe_dir + tier + '_' + gear_type + '_' + recipe_type + '.json', 'w') as out_file:
        json.dump({"type": "nbtcrafting3:data", "recipe": recipe}, out_file, indent=4, sort_keys=True)


def get_durability(gear_tier, gear_type):
    armor_durability = [
        [0, 0, 0, 0],  # Wooden
        [0, 0, 0, 0],  # Stone
        [240, 225, 165, 195],  # Iron
        [112, 105, 77, 91],  # Golden
        [528, 495, 363, 429],  # Diamond
        [592, 555, 407, 481]  # Netherite
    ]
    tool_durability = [59, 131, 250, 32, 1561, 2031]
    tiers = ['wooden', 'stone', 'iron', 'golden', 'diamond', 'netherite']
    types = ['chestplate', 'leggings', 'helmet', 'boots', 'pickaxe', 'sword', 'shovel', 'axe', 'hoe']
    return tool_durability[tiers.index(gear_tier)] if types.index(gear_type) > 3 else armor_durability[tiers.index(gear_tier)][types.index(gear_type)]


def get_unit_cost(gear_type):
    type_costs = [8, 7, 5, 4, 3, 2, 1, 3, 2]
    types = ['chestplate', 'leggings', 'helmet', 'boots', 'pickaxe', 'sword', 'shovel', 'axe', 'hoe']
    return type_costs[types.index(gear_type)]


def get_repair_ingredient(gear_tier, sacrifice_type=None):
    repair = ['planks', 'stone_tool_materials', 'iron_ingot', 'gold_ingot', 'diamond', 'netherite_ingot']
    tiers = ['wooden', 'stone', 'iron', 'golden', 'diamond', 'netherite']
    return 'minecraft:' + (repair[tiers.index(gear_tier)] if sacrifice_type is None else gear_tier + '_' + sacrifice_type)


def get_repair_ingredient_type(gear_tier, sacrifice_type=None):
    repair_type = ['tag', 'tag', 'item', 'item', 'item', 'item']
    tiers = ['wooden', 'stone', 'iron', 'golden', 'diamond', 'netherite']
    return repair_type[tiers.index(gear_tier)] if sacrifice_type is None else 'item'


def zealous_ceil(num_float):
    regular_ceil = math.ceil(num_float)
    return regular_ceil + 1 if regular_ceil == num_float else regular_ceil


def zealous_floor(num_float):
    regular_floor = math.floor(num_float)
    return regular_floor - 1 if regular_floor == num_float else regular_floor


def get_upgrade_item(gear_tier, gear_type):
    upgrade_tiers = [None, 'wooden', 'stone', 'wooden', 'iron', 'golden']
    armor_tiers = [False, False, True, True, True, True]
    tiers = ['wooden', 'stone', 'iron', 'golden', 'diamond', 'netherite']
    types = ['chestplate', 'leggings', 'helmet', 'boots', 'pickaxe', 'sword', 'shovel', 'axe', 'hoe']
    upgrade_tier = upgrade_tiers[tiers.index(gear_tier)]
    if types.index(gear_type) > 3:
        return 'minecraft:' + upgrade_tier + '_' + gear_type if upgrade_tier is not None else None
    else:
        return 'minecraft:' + upgrade_tier + '_' + gear_type if upgrade_tier is not None and armor_tiers[tiers.index(upgrade_tier)] else None


def main():
    dir_pack = '../out/tinkerers_smithing'
    dir_recipes = dir_pack + '/data/tinkerers_smithing/recipes/'
    assert_not_dir(dir_pack)
    shutil.copytree('override', dir_pack)
    assert_dir(dir_recipes)

    with open('./template/smithing.json', 'r') as template_file:
        template = json.load(template_file)

    for gear_type in ['chestplate', 'leggings', 'helmet', 'boots', 'pickaxe', 'sword', 'shovel', 'axe', 'hoe']:
        for gear_tier in ['iron', 'golden', 'diamond', 'netherite']:

            # dework
            recipe = deepcopy(template)
            recipe['result']['item'] = 'minecraft:' + gear_tier + '_' + gear_type
            recipe['base']['item'] = 'minecraft:' + gear_tier + '_' + gear_type
            recipe['ingredient']['item'] = 'minecraft:netherite_scrap'
            recipe['base']['data']['require']["RepairCost"] = '$1..'
            recipe['result']['data']['RepairCost'] = '$((base.RepairCost + 1)/2)-1'
            write_recipe(dir_recipes, recipe, gear_tier, gear_type, 'dework')

            # repair
            recipe = deepcopy(template)
            recipe['result']['item'] = 'minecraft:' + gear_tier + '_' + gear_type
            durability = get_durability(gear_tier, gear_type)
            durability_restored = math.ceil(durability / 4.0)
            recipe['base']['item'] = 'minecraft:' + gear_tier + '_' + gear_type
            recipe['ingredient']['item'] = get_repair_ingredient(gear_tier)
            recipe['base']['data']['require']["Damage"] = '$' + str(durability_restored) + '..'
            recipe['result']['data']['Damage'] = '$max(0, base.Damage - ' + str(durability_restored) + ')'
            write_recipe(dir_recipes, recipe, gear_tier, gear_type, 'repair')

            # upgrade
            recipe = deepcopy(template)
            if get_upgrade_item(gear_tier, gear_type) is not None:
                recipe['base']['item'] = get_upgrade_item(gear_tier, gear_type)
                recipe['result']['item'] = 'minecraft:' + gear_tier + '_' + gear_type
                recipe['base']['data']['deny'] = {'Upgradeable': 0}

                if gear_tier == 'netherite':
                    for sacrifice_type in ['chestplate', 'leggings', 'helmet', 'boots', 'pickaxe', 'sword', 'shovel', 'axe', 'hoe']:
                        recipe['ingredient']['item'] = get_repair_ingredient(gear_tier, sacrifice_type)
                        recipe['ingredient']['data']['require'].pop('Damage', None)

                        sacrifice_durability = get_durability(gear_tier, sacrifice_type)
                        sacrifice_units = get_unit_cost(sacrifice_type)

                        result_durability = get_durability(gear_tier, gear_type)
                        result_units = get_unit_cost(gear_type)

                        recipe['result']['data']['Damage'] = '$max(0, (' + str(result_durability) + '-((' + str(sacrifice_durability) + '-ingredient.Damage)*' + '{:.1f}'.format(sacrifice_units * result_durability) + '/' + str(sacrifice_durability * result_units) + '))#i)'
                        write_recipe(dir_recipes, recipe, gear_tier, gear_type, 'upgrade_sacrifice_' + sacrifice_type)

                elif get_unit_cost(gear_type) <= 5:
                    recipe['ingredient']['item'] = get_repair_ingredient(gear_tier)
                    recipe['result']['data']['Damage'] = "$" + str(math.floor(get_durability(gear_tier, gear_type) * ((get_unit_cost(gear_type) - 1) / 4.0)))
                    write_recipe(dir_recipes, recipe, gear_tier, gear_type, 'upgrade')

    with open('./template/shapeless.json', 'r') as template_file:
        template = json.load(template_file)

    for gear_type in ['pickaxe', 'sword', 'shovel', 'axe', 'hoe']:
        # repair
        for gear_tier in ['wooden', 'stone']:
            recipe = deepcopy(template)
            recipe['result']['item'] = 'minecraft:' + gear_tier + '_' + gear_type

            for amount in range(1, get_unit_cost(gear_type) + 1):
                recipe['ingredients'] = [{'item': 'minecraft:' + gear_tier + '_' + gear_type}] + [{get_repair_ingredient_type(gear_tier): get_repair_ingredient(gear_tier)} for _ in range(amount)]
                durability_restored = math.ceil((get_durability(gear_tier, gear_type) * amount) / float(get_unit_cost(gear_type)))
                recipe['result']['data']['Damage'] = '$max(0, i0.Damage - ' + str(durability_restored) + ')'
                write_recipe(dir_recipes, recipe, gear_tier, gear_type, "repair" + "_" + str(amount))

        # upgrade
        for gear_tier in ['stone', 'golden', 'iron']:
            recipe = deepcopy(template)
            recipe['result']['item'] = 'minecraft:' + gear_tier + '_' + gear_type
            recipe['ingredients'] = [{'item': get_upgrade_item(gear_tier, gear_type)}] + [{get_repair_ingredient_type(gear_tier): get_repair_ingredient(gear_tier)} for _ in range(get_unit_cost(gear_type))]
            recipe['result']['data']['Damage'] = "$i0.Damage"
            write_recipe(dir_recipes, recipe, gear_tier, gear_type, "upgrade")


if __name__ == "__main__":
    main()

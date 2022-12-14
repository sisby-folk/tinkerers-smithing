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


def write_recipe(recipe_dir, out_dict, tier, gear_type, recipe_type):
    with open(recipe_dir + tier + '_' + gear_type + '_' + recipe_type + '.json', 'w') as out_file:
        json.dump(out_dict, out_file, indent=4, sort_keys=True)


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
            current_dict = deepcopy(template)
            current_dict['result']['item'] = 'minecraft:' + gear_tier + '_' + gear_type
            current_dict['base']['item'] = 'minecraft:' + gear_tier + '_' + gear_type
            current_dict['ingredient']['item'] = 'minecraft:netherite_scrap'
            # non-final
            current_dict['base']['data']['require']["RepairCost"] = '$2..'
            current_dict['result']['data']['RepairCost'] = '$((base.RepairCost + 1)/2)-1'
            write_recipe(dir_recipes, current_dict, gear_tier, gear_type, 'dework')
            # final
            current_dict['base']['data']['require']["RepairCost"] = 1
            current_dict['result']['data']['RepairCost'] = "$0"
            write_recipe(dir_recipes, current_dict, gear_tier, gear_type, 'dework_final')

            # repair
            current_dict = deepcopy(template)
            current_dict['result']['item'] = 'minecraft:' + gear_tier + '_' + gear_type
            durability = get_durability(gear_tier, gear_type)
            durability_restored = math.ceil(durability / 4.0)
            current_dict['base']['item'] = 'minecraft:' + gear_tier + '_' + gear_type
            current_dict['ingredient']['item'] = get_repair_ingredient(gear_tier)
            # non-final
            current_dict['base']['data']['require']["Damage"] = '$' + str(durability_restored) + '..'
            current_dict['result']['data']['Damage'] = '$base.Damage - ' + str(durability_restored)
            write_recipe(dir_recipes, current_dict, gear_tier, gear_type, 'repair')
            # final
            current_dict['base']['data']['require']["Damage"] = '$1..' + str(durability_restored - 1)
            current_dict['result']['data']['Damage'] = "$0"
            write_recipe(dir_recipes, current_dict, gear_tier, gear_type, 'repair_final')

            # upgrade
            current_dict = deepcopy(template)
            if get_upgrade_item(gear_tier, gear_type) is not None:
                current_dict['base']['item'] = get_upgrade_item(gear_tier, gear_type)
                current_dict['result']['item'] = 'minecraft:' + gear_tier + '_' + gear_type
                current_dict['base']['data']['deny'] = {'Upgradeable': 0}

                if gear_tier == 'netherite':
                    for sacrifice_type in ['chestplate', 'leggings', 'helmet', 'boots', 'pickaxe', 'sword', 'shovel', 'axe', 'hoe']:
                        current_dict['ingredient']['item'] = get_repair_ingredient(gear_tier, sacrifice_type)
                        current_dict['ingredient']['data']['require'].pop('Damage', None)

                        sacrifice_durability = get_durability(gear_tier, sacrifice_type)
                        sacrifice_units = get_unit_cost(sacrifice_type)

                        result_durability = get_durability(gear_tier, gear_type)
                        result_units = get_unit_cost(gear_type)

                        # Damage Calculation
                        # current_sacrifice_durability = sacrifice_durability - sacrifice_current_damage
                        # restored_units = current_sacrifice_durability / sacrifice_durability * sacrifice_units
                        # restored_durability = restored_units * result_durability / result_units
                        # result_damage = result_durability - restored_durability

                        # Piecewise Function
                        # result_damage = result_durability - ((sacrifice_durability - sacrifice_current_damage) * (sacrifice_units * result_durability) / (sacrifice_durability * result_units))
                        # result damage = maxY - (maxX - x) * (bc)/(de)
                        # x > maxX - maxY * (de)/(bc)
                        # else 0

                        final_point = sacrifice_durability - result_durability * (sacrifice_durability * result_units) / (sacrifice_units * result_durability)
                        # non-final
                        current_dict['result']['data']['Damage'] = '$(' + str(result_durability) + '-((' + str(sacrifice_durability) + '-ingredient.Damage)*' + '{:.1f}'.format(sacrifice_units * result_durability) + '/' + str(sacrifice_durability * result_units) + '))#i'
                        if final_point >= 0:
                            current_dict['ingredient']['data']['require']["Damage"] = '$' + str(zealous_ceil(final_point)) + '..'
                        write_recipe(dir_recipes, current_dict, gear_tier, gear_type, 'upgrade_sacrifice_' + sacrifice_type)
                        # final
                        if final_point >= 0:
                            if final_point < sacrifice_durability:
                                current_dict['ingredient']['data']['require']["Damage"] = '$..' + str(math.floor(final_point))
                            current_dict['result']['data']['Damage'] = '$0'
                            write_recipe(dir_recipes, current_dict, gear_tier, gear_type, 'upgrade_sacrifice_' + sacrifice_type + '_final')

                elif get_unit_cost(gear_type) <= 5:
                    current_dict['ingredient']['item'] = get_repair_ingredient(gear_tier)
                    current_dict['result']['data']['Damage'] = "$" + str(math.floor(get_durability(gear_tier, gear_type) * ((get_unit_cost(gear_type) - 1) / 4.0)))
                    write_recipe(dir_recipes, current_dict, gear_tier, gear_type, 'upgrade')

    with open('./template/shapeless.json', 'r') as template_file:
        template = json.load(template_file)

    for gear_type in ['pickaxe', 'sword', 'shovel', 'axe', 'hoe']:
        for gear_tier in ['wooden', 'stone', 'golden', 'iron']:
            for recipe_type in ['repair_single', 'repair_single_final', 'repair_double', 'repair_double_final', 'repair_triple', 'repair_triple_final', 'upgrade']:

                current_dict = deepcopy(template)
                current_dict['result']['item'] = 'minecraft:' + gear_tier + '_' + gear_type

                if recipe_type.startswith('repair'):
                    if gear_tier in ['iron', 'golden']:
                        continue

                    final = recipe_type.endswith('_final')
                    amount = 3 if 'triple' in recipe_type else (2 if 'double' in recipe_type else 1)

                    if amount > get_unit_cost(gear_type):
                        continue

                    durability = get_durability(gear_tier, gear_type)
                    current_dict['ingredients'][0]['item'] = 'minecraft:' + gear_tier + '_' + gear_type

                    for _ in range(amount):
                        current_dict['ingredients'] += [{get_repair_ingredient_type(gear_tier): get_repair_ingredient(gear_tier)}]

                    durability_restored = math.ceil((durability * amount) / float(get_unit_cost(gear_type)))
                    prev_durability_restored = math.ceil((durability * (amount - 1)) / float(get_unit_cost(gear_type)))
                    current_dict['ingredients'][0]['data']['require']["Damage"] = '$' + str(max(prev_durability_restored, 1)) + '..' + str(durability_restored - 1) if final else '$' + str(durability_restored) + '..'
                    current_dict['result']['data']['Damage'] = "$0" if final else '$i0.Damage - ' + str(durability_restored)

                if recipe_type.startswith('upgrade'):
                    if gear_tier in ['wooden']:
                        continue
                    current_dict['ingredients'][0]['item'] = get_upgrade_item(gear_tier, gear_type)
                    amount = get_unit_cost(gear_type)
                    for _ in range(amount):
                        current_dict['ingredients'] += [{get_repair_ingredient_type(gear_tier): get_repair_ingredient(gear_tier)}]

                    current_dict['result']['data']['Damage'] = "$i0.Damage"

                with open(dir_recipes + gear_tier + '_' + gear_type + '_' + recipe_type + '.json', 'w') as out_file:
                    json.dump(current_dict, out_file, indent=4, sort_keys=True)


if __name__ == "__main__":
    main()

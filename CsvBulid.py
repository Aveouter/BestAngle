import csv
import os
import time

from tqdm import tqdm

# 06：228406
# 05：228431
# 04：228444
# 03：228426
# 02：228459
# 01：228302
# 目标路径
category = ['hand','head','wound','hemostat','thyroid retractor front','tissue scissors','electrotome', 
    'detector','tweezer','porteaiguille','nesis','bistoury','aspirator','gauze','injector',
    'towel forceps','Thyroid tissue','drainage tube','Treatment bowl','glue','Sterile patches',
    'thyroid retractor back','thyroid retractor']
File_path = 'runs\detect\exp\labels'
frame_num = 228302
## read the txt to build the csvfile 
def CSV_data_create(my_list = None ,file_path = 'data_20220714_rebulit.csv' ):
    def is_csv_empty(file_path):
        with open(file_path, 'r', newline='') as csvfile:
            csvreader = csv.reader(csvfile)
            for row in csvreader:
                if row:
                    return False  # CSV 文件非空
            return True  # CSV 文件为空
    def create_if_not_exist(file_path11 = file_path):
        if not os.path.exists(file_path11):
            with open(file_path11, 'w', newline='') as csvfile:
                # 创建一个空的 CSV 文件
                csvwriter = csv.writer(csvfile)
            return False
        else:
            return True
    title =  ['frame', 
            'hand1', 'head1', 'wound1', 'hemostat1', 'thyroid retractor front1', 'tissue scissors1', 
            'electrotome1', 'detector1', 'tweezer1', 'porteaiguille1', 'nesis1', 'bistoury1', 'aspirator1', 
            'gauze1', 'injector1', 'towel forceps1', 'Thyroid tissue1', 'drainage tube1','Treatment bowl1', 'glue1', 
            'Sterile patches1', 'thyroid retractor back1', 'thyroid retractor1', 
            'hand2', 'head2', 'wound2', 'hemostat2', 'thyroid retractor front2', 'tissue scissors2',
            'electrotome2', 'detector2', 'tweezer2', 'porteaiguille2', 'nesis2', 'bistoury2',
            'aspirator2', 'gauze2', 'injector2', 'towel forceps2', 'Thyroid tissue2', 
            'drainage tube2', 'Treatment bowl2', 'glue2', 'Sterile patches2', 'thyroid retractor back2',
            'thyroid retractor2',
            'hand3', 'head3', 'wound3', 'hemostat3', 'thyroid retractor front3', 'tissue scissors3', 
            'electrotome3', 'detector3', 'tweezer3', 'porteaiguille3', 'nesis3', 'bistoury3', 
            'aspirator3', 'gauze3', 'injector3', 'towel forceps3', 'Thyroid tissue3', 'drainage tube3',
            'Treatment bowl3', 'glue3', 'Sterile patches3', 'thyroid retractor back3', 
            'thyroid retractor3',
            'hand4', 'head4', 'wound4', 'hemostat4', 'thyroid retractor front4', 'tissue scissors4',
            'electrotome4', 'detector4', 'tweezer4', 'porteaiguille4', 'nesis4', 'bistoury4', 
            'aspirator4', 'gauze4', 'injector4', 'towel forceps4', 'Thyroid tissue4', 
            'drainage tube4', 'Treatment bowl4', 'glue4', 'Sterile patches4', 
            'thyroid retractor back4', 'thyroid retractor4', 
            'hand5', 'head5', 'wound5', 'hemostat5', 'thyroid retractor front5', 'tissue scissors5', 
            'electrotome5', 'detector5', 'tweezer5', 'porteaiguille5', 'nesis5', 'bistoury5', 
            'aspirator5', 'gauze5', 'injector5', 'towel forceps5', 'Thyroid tissue5', 'drainage tube5', 
            'Treatment bowl5', 'glue5', 'Sterile patches5', 'thyroid retractor back5', 'thyroid retractor5', 
            'hand6', 'head6', 'wound6', 'hemostat6', 'thyroid retractor front6', 'tissue scissors6', 'electrotome6', 
            'detector6', 'tweezer6', 'porteaiguille6', 'nesis6', 'bistoury6', 'aspirator6', 'gauze6', 'injector6', 
            'towel forceps6', 'Thyroid tissue6', 'drainage tube6', 'Treatment bowl6', 'glue6', 'Sterile patches6', 
            'thyroid retractor back6', 'thyroid retractor6']  
    ## create the csv
    if create_if_not_exist() == False:
        if is_csv_empty(file_path):
            print("CSV 文件为空")
            if file_path == 'data_20220714_rebulit.csv':
                for i,_ in enumerate(title):
                    context = ['x_label','y_label','length_label','width_label']
                    if _ == 'frame':
                        continue
                    title[i] = [title[i]+ '_' + c for c in context]
                Rtitle =  [item for sublist in title for item in (sublist if isinstance(sublist, list) else [sublist])]
                with open(file_path, 'w',newline='') as file:
                    writer = csv.writer(file)    
                    writer.writerow(Rtitle)
            else:
                title_num = category
                for i,_ in enumerate(category):
                    context = 'num'
                    NUMM = ['1','2','3','4','5','6']
                    if _ == 'frame':
                        continue
                    title_num[i] = [title_num[i]+NUMMc+'_' + context for NUMMc in NUMM]
                Rtitle =  [item for sublist in title_num for item in (sublist if isinstance(sublist, list) else [sublist])]
                with open(file_path, 'w',newline='') as file:
                    writer = csv.writer(file)    
                    writer.writerow(Rtitle)
        
    elif my_list is not None:
        if file_path == 'data_20220714_rebulit.csv':
            with open(file_path, 'a',newline='') as file:
                writer = csv.writer(file) 
                if len(my_list) == (len(title)*4-3):
                    writer.writerow(my_list)
        else:
            with open(file_path, 'a',newline='') as file:
                writer = csv.writer(file) 
                if len(my_list) == (len(category)*6):
                    writer.writerow(my_list)
                else:
                    print('error in lenofdata')
            # for lists in my_list:
            #     if len(lists) == (len(title)*4-3):
            #         writer.writerow(lists)
            #     else :
            #         print('error in lenofdata')

# dealing txt
def deal_txt(namefile,mode = True):
    # namefile = os.path.join(File_path, file_path)
    with open(namefile, 'r') as file:
        lines = [line.strip().split() for line in file]
    DATA_LIST = [0]*(4*len(category))
    category_NUM = [0]*len(category)
    for i in range(len(category)):
        ListAve = lines
        ListAve = [list(map(float, line)) for line in lines if line[0] == str(i)]
        if mode == True:
            if ListAve:
                averages_list =  [sum(col) / len(col) for col in zip(*ListAve)]
                averages_list[0] = int(averages_list[0])
                
            else:
                averages_list = [int(i),0,0,0,0]   # use 0 to padding all values
            DATA_LIST[i*4:i*4+4] = averages_list[1:]
        else:
            category_NUM[i] = len(ListAve)

    if mode == True:
        return DATA_LIST 
    else:
        return category_NUM


def main_bulit(target_path =  File_path):
    file_count = [0]*6
    CSV_data_create()
    ## 
    # List = []
    for j in tqdm(range(191105,frame_num+1), desc='Processing'):
        time.sleep(0.1)
        List_part = []
        for i in range(6):
            namefile = 'D0'+ str(i+1)+'_'+'20220714'+'_'+str(j+1)+'.txt'
            file_path = os.path.join(target_path, namefile)
            if not os.path.exists(file_path):
                # print(f'Error: {namefile} not found')
                file_count[i] =file_count[i] + 1
                List_part.append([0]*(4*len(category)))
            else:
                List_part.append(deal_txt(file_path))
        List_part = [item for sublist in List_part for item in sublist]
        List_part.insert(0,j+1)
        # List.append(List_part)
        CSV_data_create(my_list=List_part)
    print('data has been created')
    return file_count

def add_ca_NUM(target_path =  File_path):
    CSV_data_create(file_path= 'data_category_num.csv')
    for j in tqdm(range(44283,44286), desc='Processing'):
        time.sleep(0.1)
        List_part = []
        for i in range(6):
            namefile = 'D0'+ str(i+1)+'_'+'20220714'+'_'+str(j+1)+'.txt'
            file_path = os.path.join(target_path, namefile)
            if not os.path.exists(file_path):
                List_part.append([0]*len(category))
            else:
                List_part.append(deal_txt(file_path,False))
        List_part = [item for sublist in List_part for item in sublist]
        # List.append(List_part)
        print(List_part)
        # CSV_data_create(my_list=List_part,file_path= 'data_category_num.csv')
    print('data has been created')

if __name__ == "__main__":
#     error_num = main_bulit()
#     for i in range(len(error_num)):
#         print(f'the empty frame of {i+1}:{error_num[i]}')
    # add_ca_NUM()
    numbers = '4, 1, 3, 3, 2, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 2, 1, 2, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 6, 1, 1, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 2, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 3, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 4, 3, 1, 1, 0, 0, 2, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0'
    numbers_without_spaces = numbers.replace(" ", "")
    print(numbers_without_spaces)